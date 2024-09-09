package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapperImpl;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static school.faang.user_service.util.PremiumFabric.getPremium;
import static school.faang.user_service.util.PremiumFabric.getUser;

class PremiumControllerTest {
    private static final long USER_ID = 1L;
    private static final long PREMIUM_ID = 1L;
    private static final PremiumPeriod PERIOD = PremiumPeriod.MONTH;
    private static final LocalDateTime START_DATE = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime END_DATE = START_DATE.plusDays(PERIOD.getDays());

    @Mock
    private PremiumService premiumService;

    @Mock
    private UserContext userContext;

    @Spy
    private PremiumMapperImpl premiumMapper;

    @InjectMocks
    private PremiumController premiumController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = openMocks(this);
    }

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        var user = getUser(USER_ID);
        var premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);
        when(premiumService.buyPremium(USER_ID, PERIOD)).thenReturn(premium);
        when(userContext.getUserId()).thenReturn(USER_ID);
        var responsePremiumDto = premiumController.buyPremium(PERIOD.getDays());
        assertThat(responsePremiumDto)
                .isNotNull()
                .isInstanceOf(ResponsePremiumDto.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
}