package school.faang.user_service.sheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.PremiumService;
import school.faang.user_service.service.UserRatingService;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PremiumSchedulerTest {

    @InjectMocks
    private PremiumScheduler premiumScheduler;

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserRatingService userRatingService;

    private final LocalDateTime before = LocalDateTime.now().minusMonths(12);

    @Test
    void testCheckPremium() {
        Premium premium1 = new Premium(1L, User.builder().id(1L).build(), before.plusMonths(1), before.plusMonths(4));
        Premium premium2 = new Premium(2L, User.builder().id(1L).build(), before.plusMonths(12), before.plusMonths(15));
        Mockito.when(premiumService.findAllPremium()).thenReturn(List.of(premium1, premium2));

        premiumScheduler.checkPremium();

        Mockito.verify(premiumService).delete(premium1);
        Mockito.verify(userRatingService).depriveRatingEndPremium(1L);
    }
}