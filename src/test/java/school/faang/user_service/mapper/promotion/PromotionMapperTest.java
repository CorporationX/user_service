package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;

import static org.junit.jupiter.api.Assertions.*;

class PromotionMapperTest {

    private final PromotionMapper promotionMapper = Mappers.getMapper(PromotionMapper.class);

    @Test
    @DisplayName("Should map Promotion to PromotionDto correctly")
    void testToDto() {
        User user = new User();
        user.setId(1L);

        Promotion promotion = new Promotion();
        promotion.setId(100L);
        promotion.setPriority(5);
        promotion.setShowCount(10);
        promotion.setUser(user);

        PromotionDto dto = promotionMapper.toDto(promotion);

        assertNotNull(dto);
        assertEquals(5, dto.getPriority());
        assertEquals(10, dto.getShowCount());
        assertEquals(1L, dto.getUserId());
    }

    @Test
    @DisplayName("Should map PromotionDto to Promotion correctly")
    void testToEntity() {
        PromotionDto dto = new PromotionDto();
        dto.setPriority(5);
        dto.setShowCount(10);
        dto.setUserId(1L);

        Promotion promotion = promotionMapper.toEntity(dto);

        assertNotNull(promotion);
        assertEquals(5, promotion.getPriority());
        assertEquals(10, promotion.getShowCount());
        assertNotNull(promotion.getUser());
        assertEquals(1L, promotion.getUser().getId());
    }
}
