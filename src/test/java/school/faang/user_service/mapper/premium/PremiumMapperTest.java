package school.faang.user_service.mapper.premium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PremiumMapperTest {

    private final PremiumMapper premiumMapper = Mappers.getMapper(PremiumMapper.class);

    @Test
    @DisplayName("Should map Premium to PremiumDto correctly")
    void testToPremiumDto() {
        User user = new User();
        user.setId(1L);

        Premium premium = new Premium();
        premium.setId(100L);
        premium.setStartDate(LocalDateTime.of(2023, 10, 1, 12, 0));
        premium.setEndDate(LocalDateTime.of(2024, 10, 1, 12, 0));
        premium.setUser(user);

        PremiumDto dto = premiumMapper.toPremiumDto(premium);

        assertNotNull(dto);
        assertEquals(1L, dto.getUserId());
        assertEquals(LocalDateTime.of(2023, 10, 1, 12, 0), dto.getStartDate());
        assertEquals(LocalDateTime.of(2024, 10, 1, 12, 0), dto.getEndDate());
    }

    @Test
    @DisplayName("Should map PremiumDto to Premium correctly")
    void testToEntity() {
        PremiumDto dto = PremiumDto.builder()
                .userId(1L)
                .startDate(LocalDateTime.of(2023, 10, 1, 12, 0))
                .endDate(LocalDateTime.of(2024, 10, 1, 12, 0))
                .build();

        Premium premium = premiumMapper.toEntity(dto);

        assertNotNull(premium);
        assertEquals(LocalDateTime.of(2023, 10, 1, 12, 0), premium.getStartDate());
        assertEquals(LocalDateTime.of(2024, 10, 1, 12, 0), premium.getEndDate());
    }
}
