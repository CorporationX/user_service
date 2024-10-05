package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@EnableAsync
@ExtendWith(MockitoExtension.class)
class PremiumManagementServiceTest {

    @InjectMocks
    private PremiumManagementService premiumManagementService;

    @Mock
    private PremiumRepository premiumRepository;

    private Premium premium1;
    private Premium premium2;

    @BeforeEach
    void setUp() {
        premium1 = new Premium();
        premium2 = new Premium();

        ReflectionTestUtils.setField(premiumManagementService, "removerBatchSize", 2);
    }

    @Nested
    class RemoveExpiredPremiumsTests {

        @Test
        @DisplayName("Successfully remove expired premiums in batches")
        void whenRemoveExpiredPremiumsThenSuccess() {
            List<Premium> expiredPremiums = List.of(premium1, premium2);
            when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(expiredPremiums);

            premiumManagementService.removeExpiredPremiums();

            verify(premiumRepository).findAllByEndDateBefore(any(LocalDateTime.class));
            verify(premiumRepository).deleteAllInBatch(expiredPremiums);
        }

        @Test
        @DisplayName("Does nothing if no expired premiums found")
        void whenNoExpiredPremiumsThenDoNothing() {
            when(premiumRepository.findAllByEndDateBefore(any())).thenReturn(List.of());

            premiumManagementService.removeExpiredPremiums();

            verify(premiumRepository).findAllByEndDateBefore(any());
            verify(premiumRepository, never()).deleteAllInBatch(anyList());
        }
    }

    @Nested
    class RemoveExpiredPremiumsByBatchesTests {

        @Test
        @DisplayName("Successfully remove expired premiums by batches")
        void whenRemoveExpiredPremiumsByBatchesThenSuccess() {
            List<Premium> expiredPremiums = List.of(premium1, premium2);

            premiumManagementService.removeExpiredPremiumsByBatches(expiredPremiums);

            verify(premiumRepository).deleteAllInBatch(expiredPremiums);
        }

        @Test
        @DisplayName("Does nothing if empty batch provided")
        void whenEmptyBatchThenDoNothing() {
            premiumManagementService.removeExpiredPremiumsByBatches(List.of());

            verify(premiumRepository, never()).deleteAllInBatch(anyList());
        }
    }
}
