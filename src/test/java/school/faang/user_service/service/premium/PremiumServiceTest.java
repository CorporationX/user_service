package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {

    @InjectMocks
    private PremiumService premiumService;

    @Mock
    private PremiumRepository premiumRepository;

    @Captor
    private ArgumentCaptor<List<Premium>> premiumCaptor;


    private List<Premium> expiredPremiums;

    @BeforeEach
    void setUp() {
        expiredPremiums = List.of(
                new Premium(1L, null, LocalDateTime.now().minusMonths(2), LocalDateTime.now().minusMonths(1)),
                new Premium(2L, null, LocalDateTime.now().minusMonths(3), LocalDateTime.now().minusMonths(2))
        );
        ReflectionTestUtils.setField(premiumService, "batchSize", 100);
        ReflectionTestUtils.setField(premiumService, "threadPoolSize", 4);
    }

    @Test
    void removeExpiredPremiums_ShouldDeleteExpiredRecords() throws InterruptedException {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(expiredPremiums);

        premiumService.removeExpiredPremiums();
        Thread.sleep(500);
        verify(premiumRepository, times(1)).deleteAllInBatch(premiumCaptor.capture());
        Assertions.assertEquals(expiredPremiums, premiumCaptor.getValue());
    }

    @Test
    void removeExpiredPremiums_ShouldNotCallDelete_WhenNoExpiredRecords() {
        when(premiumRepository.findAllByEndDateBefore(any(LocalDateTime.class)))
                .thenReturn(List.of());

        premiumService.removeExpiredPremiums();

        verify(premiumRepository, never()).deleteAllByIdInBatch(anyList());
    }
}

