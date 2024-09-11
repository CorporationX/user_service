package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.ResponseEventPromotionDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;

class EventPromotionMapperTest {
    private static final long EVENT_PROMOTION_ID = 1;
    private static final long EVENT_ID = 1;
    private static final int NUMBER_OF_VIEWS = PromotionTariff.STANDARD.getNumberOfViews();
    private static final int AUDIENCE_REACH = PromotionTariff.STANDARD.getAudienceReach();
    private static final LocalDateTime DATE = LocalDateTime.now();

    private final EventPromotionMapper eventPromotionMapper = Mappers.getMapper(EventPromotionMapper.class);

    @Test
    @DisplayName("Given dto and successful map")
    void testToDto() {
        Event event = getEvent(EVENT_ID);
        EventPromotion eventPromotion = getEventPromotion(EVENT_PROMOTION_ID, event, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);
        var responseDto = new ResponseEventPromotionDto(EVENT_PROMOTION_ID, EVENT_ID, NUMBER_OF_VIEWS,
                AUDIENCE_REACH, DATE);

        assertThat(eventPromotionMapper.toDto(eventPromotion)).isEqualTo(responseDto);
    }
}