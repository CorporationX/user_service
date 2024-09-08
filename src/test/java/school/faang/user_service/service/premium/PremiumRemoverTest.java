package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.scheduler.PremiumRemover;
import school.faang.user_service.scheduler.PremiumService;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumRemoverTest {

    @Mock
    PremiumService premiumService;

    @InjectMocks
    PremiumRemover premiumRemover;

    @Test
    public void testRemovePremiumIsSuccessful() {

        premiumRemover.removePremium();

        verify(premiumService, times(1)).removePremium(anyInt());
    }
}
