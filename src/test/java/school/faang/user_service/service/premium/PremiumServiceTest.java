package school.faang.user_service.service.premium;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;
import school.faang.user_service.scheduler.PremiumService;

import java.time.LocalDateTime;
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

    List<Premium> testBatch = List.of(new Premium());

    @Test
    public void testDefineExpiredPremiumIsSuccessful() {

        premiumService.defineExpiredPremium();

        verify(premiumRepository, times(1)).findAllByEndDateBefore(any(LocalDateTime.class));
    }

    @Test
    public void testRemovePremiumIsSuccessful() {

        premiumService.removePremium(testBatch);

        verify(premiumRepository, times(1)).deleteAll(testBatch);
    }
}
