package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.EventResponseDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.promotion.EventPromotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapperPromotion {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(source = "promotion", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotion", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    EventResponseDto toEventResponseDto(Event event);

    @Named("mapTariff")
    default String mapTariff(EventPromotion eventPromotion) {
        if (eventPromotion == null) {
            return "Don't have promotion";
        }
        return eventPromotion.getPromotionTariff().toString();
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(EventPromotion eventPromotion) {
        if (eventPromotion == null) {
            return null;
        }
        return eventPromotion.getNumberOfViews();
    }
}
