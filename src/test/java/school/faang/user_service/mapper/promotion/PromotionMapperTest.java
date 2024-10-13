package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.dto.promotion.PromotionTarget;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;

import static org.junit.jupiter.api.Assertions.*;

class PromotionMapperTest {

    private final PromotionMapper promotionMapper = Mappers.getMapper(PromotionMapper.class);

    @Test
    @DisplayName("Should map Promotion to PromotionDto correctly")
    void testToPromotionDto() {
        User user = new User();
        user.setId(1L);

        Promotion promotion = Promotion.builder()
                .id(1L)
                .priority(5)
                .showCount(10)
                .user(user)
                .target(PromotionTarget.EVENTS.toString())
                .build();

        PromotionDto dto = promotionMapper.toPromotionDto(promotion);

        assertNotNull(dto);
        assertEquals(5, dto.getPriority());
        assertEquals(10, dto.getShowCount());
        assertEquals(PromotionTarget.EVENTS, dto.getTarget());
    }

    @Test
    @DisplayName("Should map PromotionDto to Promotion correctly")
    void testToEntity() {
        PromotionDto dto = PromotionDto.builder()
                .priority(5)
                .showCount(10)
                .userId(1L)
                .target(PromotionTarget.EVENTS)
                .build();

        Promotion promotion = promotionMapper.toEntity(dto);

        assertNotNull(promotion);
        assertEquals(5, promotion.getPriority());
        assertEquals(10, promotion.getShowCount());
        assertEquals(PromotionTarget.EVENTS.toString(), promotion.getTarget());
    }
}
