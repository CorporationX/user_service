package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapperPromotion {

    @Mapping(source = "promotion", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotion", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    UserResponseDto toUserResponseDto(User user);

    @Named("mapTariff")
    default String mapTariff(UserPromotion userPromotion) {
        if (userPromotion == null) {
            return "Don't have promotion";
        }
        return userPromotion.getPromotionTariff().toString();
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(UserPromotion userPromotion) {
        if (userPromotion == null) {
            return null;
        }
        return userPromotion.getNumberOfViews();
    }
}
