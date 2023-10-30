package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PremiumMapperTest {

    @Spy
    private PremiumMapperImpl premiumMapper;

    private Premium premium;

    private PremiumDto premiumDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder().id(1L).build();
        premium = new Premium(1L, user, now, now.plusMonths(3));
        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(now)
                .endDate(now.plusMonths(3))
                .build();
    }

    @Test
    void testToDto() {
        PremiumDto toDto = premiumMapper.toDto(premium);
        assertEquals(premiumDto, toDto);
    }

    @Test
    void testToEntity() {
        Premium toEntity = premiumMapper.toEntity(premiumDto);
        assertEquals(premium, toEntity);
    }
}