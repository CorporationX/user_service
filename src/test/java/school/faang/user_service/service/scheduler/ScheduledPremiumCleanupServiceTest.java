package school.faang.user_service.service.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.service.AsyncPremiumCleanupService;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ScheduledPremiumCleanupServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private ScheduledPremiumCleanupService premiumCleanupService;
    @Mock
    private AsyncPremiumCleanupService asyncPremiumCleanupService;

    @Test
    public void cleanExpiredPremiums() {
        when(asyncPremiumCleanupService.findExpiredPremiums()).thenReturn(List.of(List.of(new Premium())));
        premiumCleanupService.cleanExpiredPremiums();
        verify(asyncPremiumCleanupService, times(1)).findExpiredPremiums();
        verify(asyncPremiumCleanupService, times(1)).cleanExpiredPremiumsAsync(any());
    }
}
