package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PremiumCleanupServiceTest {

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumCleanupService premiumCleanupService;

    @Test
    void cleanExpiredPremiums_callRepositoryMethods() {
        List<Premium> expiredSubscriptions = List.of(
                new Premium(1, null, LocalDateTime.now(), LocalDateTime.now().minusDays(1)),
                new Premium(2, null, LocalDateTime.now(), LocalDateTime.now().minusDays(2))
        );

        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(expiredSubscriptions);

        premiumCleanupService.cleanExpiredPremiums();

        verify(premiumRepository).findAllByEndDateBefore(any(LocalDateTime.class));
        verify(premiumRepository).deleteAll(expiredSubscriptions);
    }
}
