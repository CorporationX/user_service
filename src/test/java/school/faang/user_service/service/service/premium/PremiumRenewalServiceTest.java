package school.faang.user_service.service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.premium.PremiumNotificationDto;
import school.faang.user_service.dto.premium.PremiumRequestDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.kafka.publisher.KafkaPublisher;
import school.faang.user_service.service.premium.PremiumBuyingService;
import school.faang.user_service.service.premium.PremiumRenewalService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumRenewalServiceTest {

    @InjectMocks
    private PremiumRenewalService premiumRenewalService;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private PremiumBuyingService premiumBuyingService;

    private User user;
    private final Country country = new Country(1L, "Title", Collections.emptyList());
    private final String premiumExpiredTopic = "topic1";
    private final String premiumExpireSoonTopic = "topic2";

    @BeforeEach
    public void setUp() {
        user = User.builder().id(1L).username("name").country(country).build();
        ReflectionTestUtils.setField(premiumRenewalService, "premiumExpiredTopic", premiumExpiredTopic);
        ReflectionTestUtils.setField(premiumRenewalService, "premiumExpiryNotificationDays", 2);
        ReflectionTestUtils.setField(premiumRenewalService, "premiumExpireSoonTopic", premiumExpireSoonTopic);
    }

    @Test
    public void testUpdatePremiumForUsers_withoutAutoRenew_expired() {
        List<User> users = new ArrayList<>();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .endDate(LocalDateTime.now().plusDays(1))
                .autoRenew(false)
                .build();

        user.setPremium(premium);
        users.add(user);

        premiumRenewalService.updatePremiumForUsers(users);

        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumExpiredTopic));
    }

    @Test
    public void testUpdatePremiumForUsers_withoutAutoRenew_expireSoon() {
        List<User> users = new ArrayList<>();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .endDate(LocalDateTime.now().minusMinutes(1))
                .autoRenew(false)
                .build();

        user.setPremium(premium);
        users.add(user);

        premiumRenewalService.updatePremiumForUsers(users);

        verify(kafkaPublisher, times(1))
                .sendInTransaction(any(PremiumNotificationDto.class), eq(premiumExpireSoonTopic));
    }

    @Test
    public void testUpdatePremiumForUsers_withAutoRenew() {
        List<User> users = new ArrayList<>();
        LocalDateTime startDate = LocalDateTime.now();
        Premium premium = Premium.builder()
                .id(1L)
                .user(user)
                .startDate(startDate)
                .endDate(startDate.plusMonths(3))
                .autoRenew(true)
                .build();

        user.setPremium(premium);
        users.add(user);

        premiumRenewalService.updatePremiumForUsers(users);

        verify(premiumBuyingService, times(1))
                .buyPremium(any(PremiumRequestDto.class), eq(false));
    }
}
