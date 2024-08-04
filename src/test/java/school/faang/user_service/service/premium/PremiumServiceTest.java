package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    @Mock
    private PremiumRepository premiumRepository;
    @InjectMocks
    private PremiumService premiumService;
    int batchSize = 10;

    @Test
    @DisplayName("blankListTest")
    void testRemovingExpiredPremiumAccessEmpty(){
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        premiumService.removingExpiredPremiumAccess(batchSize);

        verify(premiumRepository, Mockito.times(1))
                .findAllByEndDateBefore(any(LocalDateTime.class));
        verifyNoMoreInteractions(premiumRepository);
    }

    @Test
    @DisplayName("RemovingExpiredPremiumAccessValid")
    void testRemovingExpiredPremiumAccessValid(){
        Premium premium = Premium.builder()
                .id(1L)
                .endDate(LocalDateTime.now().minusDays(1))
                .build();
        List<Long> premiumId = List.of(1L);
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(List.of(premium));
        doNothing().when(premiumRepository).deleteAllById(premiumId);

        premiumService.removingExpiredPremiumAccess(10);

        verify(premiumRepository, times(1))
                .findAllByEndDateBefore(any(LocalDateTime.class));
        verify(premiumRepository, times(1))
                .deleteAllById(anyList());
    }
}