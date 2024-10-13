package school.faang.user_service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.PremiumService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumRemoverTest {

    @Mock
    private PremiumService premiumService;

    @InjectMocks
    private PremiumRemover premiumRemover;

    @Test
    void testTriggerRemoveSchedulerOk(){
        premiumRemover.removePremium();

        verify(premiumService).deleteExpiredPremiums();
    }
}
