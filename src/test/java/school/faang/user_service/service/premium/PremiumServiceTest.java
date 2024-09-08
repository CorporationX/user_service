package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.scheduler.PremiumService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    PremiumRepository premiumRepository;

    @InjectMocks
    PremiumService premiumService;

    int testBatchSize = 30;

    @Test
    public void testRemovePremiumIsSuccessful() {

        premiumService.removePremium(testBatchSize);

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }
}
