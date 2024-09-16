package school.faang.user_service.mapper.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.premium.util.Premium;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.premium.PremiumFabric.getPremium;
import static school.faang.user_service.util.premium.PremiumFabric.getResponsePremiumDto;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;

class PremiumMapperTest {
    private static final long PREMIUM_ID = 1;
    private static final long USER_ID = 1;
    private static final LocalDateTime START_DATE = LocalDateTime.now();
    private static final LocalDateTime END_DATE = START_DATE.plusDays(31);

    private final ResponsePremiumMapper premiumMapper = Mappers.getMapper(ResponsePremiumMapper.class);

    @Test
    @DisplayName("Given dto and successful map")
    void testToDto() {
        User user = getUser(USER_ID);
        Premium premium = getPremium(PREMIUM_ID, user, START_DATE, END_DATE);
        ResponsePremiumDto responsePremiumDto = getResponsePremiumDto(PREMIUM_ID, USER_ID, START_DATE, END_DATE);

        assertThat(premiumMapper.toDto(premium)).isEqualTo(responsePremiumDto);
    }

}