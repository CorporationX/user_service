package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.enums.PremiumPeriod;
import school.faang.user_service.service.premium.PremiumService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PremiumControllerTest {

    @Mock
    private PremiumService service;

    @InjectMocks
    private PremiumController controller;

    @Test
    void buyPremium() {
        controller.buyPremium(1, 30);
        Mockito.verify(service, Mockito.times(1)).buyPremium(1, PremiumPeriod.fromDays(30));
    }
}