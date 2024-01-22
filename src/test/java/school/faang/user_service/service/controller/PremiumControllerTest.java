package school.faang.user_service.service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.controller.PremiumController;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.service.PremiumService;

@ExtendWith(MockitoExtension.class)
public class PremiumControllerTest {

    @Mock
    private PremiumService premiumService;

    @Spy
    UserContext userContext;

    @InjectMocks
    private PremiumController premiumController;

    @BeforeEach
    public void init() {
       userContext.setUserId(1L);
    }

    @Test
    public void testBuyPremium() {
        premiumController.buyPremium(30);
        Mockito.verify(premiumService, Mockito.times(1))
                .buyPremium(1L, PremiumPeriod.ONE_MONTH);
    }

}
