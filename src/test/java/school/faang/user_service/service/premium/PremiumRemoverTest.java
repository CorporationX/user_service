package school.faang.user_service.service.premium;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

class PremiumRemoverTest {

    private PremiumService premiumService;
    private PremiumRemover premiumRemover;

    @BeforeEach
    void setUp() {
        premiumService = mock(PremiumService.class);
        premiumRemover = new PremiumRemover(premiumService);
    }

    @Test
    void shouldCallRemoveExpiredPremiums() {
        premiumRemover.removePremium();

        verify(premiumService).removeExpiredPremiums();
    }
}
