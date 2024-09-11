package school.faang.user_service.controller.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.entity.premium.PremiumPeriod;
import school.faang.user_service.mapper.premium.PremiumMapper;
import school.faang.user_service.service.premium.PremiumService;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.premium.PremiumFabric.getResponsePremiumDto;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;

@ExtendWith(MockitoExtension.class)
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
    private PremiumMapper premiumMapper = Mappers.getMapper(PremiumMapper.class);

    @InjectMocks
    private PremiumController premiumController;

    @Test
    @DisplayName("Buy premium successful")
    void testBuyPremiumSuccessful() {
        User user = getUser(USER_ID);
        Premium premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);
        ResponsePremiumDto expectedResponsePremiumDto = getResponsePremiumDto(PREMIUM_ID, user.getId(),
                START_DATE, END_DATE);
        when(premiumService.buyPremium(USER_ID, PERIOD)).thenReturn(premium);
        when(userContext.getUserId()).thenReturn(USER_ID);
        ResponsePremiumDto responsePremiumDto = premiumController.buyPremium(PERIOD.getDays());

        assertThat(responsePremiumDto)
                .isNotNull()
                .isEqualTo(expectedResponsePremiumDto);
    }
}