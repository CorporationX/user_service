package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsyncPremiumCleanupServiceTest {
    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private AsyncPremiumCleanupService asyncPremiumCleanupService;

    @Test
    void cleanExpiredPremiumsAsync() {
        List<Premium> premiums = List.of(Premium.builder().id(1L).build());

        asyncPremiumCleanupService.cleanExpiredPremiumsAsync(premiums);

        verify(premiumRepository, times(1)).deleteAll(premiums);
    }

    @Test
    void findExpiredPremiums() {
        ReflectionTestUtils.setField(asyncPremiumCleanupService, "premiumBatchSize", 1);
        List<Premium> premiums = List.of(Premium.builder().id(1L).build());
        when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(premiums);
        asyncPremiumCleanupService.findExpiredPremiums();
        verify(premiumRepository, times(1)).findAllByEndDateBefore(any());
    }
}