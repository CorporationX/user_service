package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PremiumMapperTest {

    private PremiumMapper premiumMapper = Mappers.getMapper(PremiumMapper.class);

    @Test
    void testPremiumToDto() {
        Premium premium = new Premium();
        premium.setId(1L);
        premium.setStartDate(LocalDateTime.of(2023, 1, 1, 0, 0));
        premium.setEndDate(LocalDateTime.of(2023, 1, 31, 0, 0));
        User user = new User();
        user.setId(2L);
        premium.setUser(user);

        PremiumDto premiumDto = premiumMapper.toDto(premium);

        assertEquals(premium.getId(), premiumDto.getId());
        assertEquals(premium.getUser().getId(), premiumDto.getUserId());
        assertEquals(30, premiumDto.getDays());
    }

    @Test
    void testDtoToPremium() {
        PremiumDto premiumDto = new PremiumDto();
        premiumDto.setId(1L);
        premiumDto.setUserId(2L);

        Premium premium = premiumMapper.toEntity(premiumDto);

        assertEquals(premiumDto.getId(), premium.getId());
        assertEquals(premiumDto.getUserId(), premium.getUser().getId());
    }
}
