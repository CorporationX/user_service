package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PremiumServiceTest {

    @Mock
    PremiumRepository premiumRepository;

    @InjectMocks
    PremiumService premiumService;

    List<Premium> testPremiums = new ArrayList<>();
    List<Premium> testBatch = new ArrayList<>();
    int testBatchSize = 30;

    @Test
    public void testRemovePremiumIsSuccessful() {

        premiumService.removePremium(testBatchSize);

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }
}
