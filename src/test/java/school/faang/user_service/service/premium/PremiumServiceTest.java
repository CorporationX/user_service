package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumService premiumService;

    @BeforeEach
    public void setUp() throws Exception {
        Field poolSizeField = PremiumService.class.getDeclaredField("poolSize");
        poolSizeField.setAccessible(true);
        poolSizeField.set(premiumService, 2);
        premiumService.init();
    }

    @Test
    public void testRemoveExpiredPremiums_mocked() {
        LocalDateTime now = LocalDateTime.now();
        User testUser = User.builder().id(1L).build();

        List<Premium> allPremiums = List.of(
                Premium.builder().user(testUser).startDate(now).endDate(now.minusDays(10)).build(),
                Premium.builder().user(testUser).startDate(now).endDate(now.minusDays(10)).build(),
                Premium.builder().user(testUser).startDate(now).endDate(now.minusDays(10)).build()
        );

        Mockito.when(premiumRepository.findAllByEndDateBefore(Mockito.any()))
                .thenReturn(allPremiums);

        premiumService.removeExpiredPremiums(2);

        Mockito.verify(premiumRepository, Mockito.atLeastOnce())
                .deleteAll(Mockito.anyList());
    }

}