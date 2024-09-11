package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.ResponseEventPromotionDto;
import school.faang.user_service.dto.promotion.ResponseUserPromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;
import static school.faang.user_service.util.promotion.PromotionFabric.getUserPromotion;

class UserPromotionMapperTest {
    private static final long EVENT_PROMOTION_ID = 1;
    private static final long USER_ID = 1;
    private static final int NUMBER_OF_VIEWS = PromotionTariff.STANDARD.getNumberOfViews();
    private static final int AUDIENCE_REACH = PromotionTariff.STANDARD.getAudienceReach();
    private static final LocalDateTime DATE = LocalDateTime.now();

    private final UserPromotionMapper userPromotionMapper = Mappers.getMapper(UserPromotionMapper.class);

    @Test
    @DisplayName("Given dto and successful map")
    void testToDto() {
        User user = getUser(USER_ID);
        UserPromotion userPromotion = getUserPromotion(EVENT_PROMOTION_ID, user, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);
        var responseDto = new ResponseUserPromotionDto(EVENT_PROMOTION_ID, USER_ID, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);

        assertThat(userPromotionMapper.toDto(userPromotion)).isEqualTo(responseDto);
    }
}