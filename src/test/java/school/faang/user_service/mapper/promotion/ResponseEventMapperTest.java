package school.faang.user_service.mapper.promotion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.promotion.EventResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;
import school.faang.user_service.entity.promotion.PromotionTariff;

import static org.assertj.core.api.Assertions.assertThat;
import static school.faang.user_service.util.premium.PremiumFabric.getUser;
import static school.faang.user_service.util.promotion.PromotionFabric.getEvent;
import static school.faang.user_service.util.promotion.PromotionFabric.getEventPromotion;

class ResponseEventMapperTest {
    private static final long USER_ID = 1;
    private static final long EVENT_ID = 1;
    private static final String TITLE = "test title";
    private static final PromotionTariff TARIFF = PromotionTariff.STANDARD;

    private final EventMapperPromotion responseEventMapper = Mappers.getMapper(EventMapperPromotion.class);

    @Test
    @DisplayName("Test converting event to response event")
    void testToDto() {
        User user = getUser(USER_ID);
        EventPromotion eventPromotion = getEventPromotion(TARIFF, TARIFF.getNumberOfViews());
        Event event = getEvent(EVENT_ID, TITLE, user, eventPromotion);
        var responseDto = new EventResponseDto(EVENT_ID, TITLE, USER_ID, TARIFF.toString(), TARIFF.getNumberOfViews());

        assertThat(responseEventMapper.toEventResponseDto(event)).isEqualTo(responseDto);
    }
}