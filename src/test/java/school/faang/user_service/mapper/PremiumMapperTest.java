package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PremiumMapperTest {
    @Spy
    private PremiumMapperImpl premiumMapper;
    private PremiumDto premiumDto;
    private Premium premium;

    @BeforeEach
    void setUp() {
        LocalDateTime startDate = LocalDateTime.of(2020, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2020, 1, 31, 0, 0);
        premiumDto = PremiumDto.builder()
                .id(1L)
                .userId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        premium = Premium.builder()
                .id(1L)
                .user(User.builder().id(1L).build())
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }

    @Test
    void testToDto() {
        assertEquals(premiumDto, premiumMapper.toDto(premium));
    }

    @Test
    void testToEntity() {
        premium.setUser(null);
        assertEquals(premium, premiumMapper.toEntity(premiumDto));
    }
}