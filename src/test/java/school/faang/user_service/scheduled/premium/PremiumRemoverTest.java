package school.faang.user_service.scheduled.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumRemoverTest {
    @Mock
    private PremiumService premiumService;
    @InjectMocks
    private PremiumRemover premiumRemover;
    private int batchSize = 10;

    @Test
    @DisplayName("gettingBlankList")
    void testRemovePremiumBlankList() {
        when(premiumService.removingExpiredPremiumAccess(anyInt()))
                .thenReturn(Collections.emptyList());

        premiumRemover.removePremium();

        verify(premiumService, times(1))
                .removingExpiredPremiumAccess(anyInt());
    }

    @Test
    @DisplayName("gettingPremiumList")
    void testRemovePremiumList() {
        List<Premium> oneList = List.of(Premium.builder().id(1L).build());
        List<List<Premium>> premiumList = List.of(oneList);

        when(premiumService.removingExpiredPremiumAccess(anyInt()))
                .thenReturn(premiumList);
        doNothing().when(premiumService).executeAsyncBatchDelete(anyList());

        premiumRemover.removePremium();

        verify(premiumService, times(1)).executeAsyncBatchDelete(anyList());
    }
}