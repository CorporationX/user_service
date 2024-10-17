package school.faang.user_service.scheduler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.premium.PremiumManagementService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension .class)
public class PremiumRemoverTest {

    @Mock
    private PremiumManagementService premiumManagementService;

    @InjectMocks
    private PremiumRemover premiumRemover;

    @Test
    @DisplayName("Test that expired premiums are removed on schedule")
    void whenTriggerRemoveSchedulerThenSuccess(){
        premiumRemover.removeExpiredPremiums();

        verify(premiumManagementService).removeExpiredPremiums();
    }
}
