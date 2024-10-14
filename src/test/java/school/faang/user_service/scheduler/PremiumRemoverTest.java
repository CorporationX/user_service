package school.faang.user_service.scheduler;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.service.impl.PremiumServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumRemoverTest {
    @Mock
    PremiumServiceImpl premiumService;

    @InjectMocks
    private PremiumRemover premiumRemover;

    private LogCaptor logCaptor;

    @BeforeEach
    public void setUp() {
        logCaptor = LogCaptor.forClass(PremiumRemover.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("Should delete premiums when expired premiums exist")
    public void removePremium_Success() {
        List<Long> batch1 = List.of(1L, 2L);
        List<Long> batch2 = List.of(3L);
        List<List<Long>> expiredPremiumsIds = Arrays.asList(batch1, batch2);

        when(premiumService.findAndSplitExpiredPremiums()).thenReturn(expiredPremiumsIds);

        when(premiumService.deleteExpiredPremiumsByIds(batch1))
                .thenReturn(CompletableFuture.completedFuture(batch1.size()));
        when(premiumService.deleteExpiredPremiumsByIds(batch2))
                .thenReturn(CompletableFuture.completedFuture(batch2.size()));

        premiumRemover.removePremium();

        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(premiumService, times(1)).findAndSplitExpiredPremiums();
        verify(premiumService, times(2)).deleteExpiredPremiumsByIds(captor.capture());

        List<List<Long>> capturedBatches = captor.getAllValues();
        assertTrue(capturedBatches.containsAll(List.of(batch1, batch2)));

        String logMessage = logCaptor.getInfoLogs().get(0);
        assertTrue(logMessage.contains("A total of 3 expired premiums were deleted at"));
    }

    @Test
    @DisplayName("Should log no action when no expired premiums exist")
    public void removePremium_NoExpiredPremiums() {
        List<List<Long>> expiredPremiumsIds = Collections.emptyList();

        when(premiumService.findAndSplitExpiredPremiums()).thenReturn(expiredPremiumsIds);

        premiumRemover.removePremium();

        verify(premiumService, times(1)).findAndSplitExpiredPremiums();
        verify(premiumService, never()).deleteExpiredPremiumsByIds(any());

        String logMessage = logCaptor.getInfoLogs().get(0);
        assertTrue(logMessage.contains("No expired premiums to delete"));
    }
}
