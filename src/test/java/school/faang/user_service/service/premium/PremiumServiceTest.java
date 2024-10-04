package school.faang.user_service.service.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.util.premium.PremiumFabric.buildPremiums;

@ExtendWith(MockitoExtension.class)
class PremiumServiceTest {
    private static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2000, 1, 1, 1, 1);
    private static final int NUMBER_OF_PREMIUMS = 3;

    @Mock
    private PremiumRepository premiumRepository;

    @InjectMocks
    private PremiumService premiumService;

    @Test
    @DisplayName("Find all premium by end date before successful")
    void testFindAllByEndDateBeforeSuccessful() {
        premiumService.findAllByEndDateBefore(LOCAL_DATE_TIME);

        verify(premiumRepository).findAllByEndDateBefore(LOCAL_DATE_TIME);
    }

    @Test
    @DisplayName("Delete all premiums by id successful")
    void testDeleteAllPremiumsByIdAsyncSuccessful() {
        List<Premium> premiums = buildPremiums(NUMBER_OF_PREMIUMS);
        premiumService.deleteAllPremiumsByIdAsync(premiums);

        verify(premiumRepository).deleteByIdInBatch(anyList());
    }
}