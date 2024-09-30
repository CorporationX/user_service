package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.scheduler.PremiumRemover;
import school.faang.user_service.service.PremiumService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumRemoverTest {

    @Mock
    PremiumService premiumService;

    @InjectMocks
    PremiumRemover premiumRemover;

    @Test
    public void testRemovePremiumIsSuccessful() {
        ReflectionTestUtils.setField(premiumRemover, "batchSize", 30);
        when(premiumService.defineExpiredPremium()).thenReturn(List.of(new Premium()));

        premiumRemover.removePremium();

        verify(premiumService, times(1)).defineExpiredPremium();
        verify(premiumService, times(1)).removePremium(List.of(new Premium()));
    }
}
