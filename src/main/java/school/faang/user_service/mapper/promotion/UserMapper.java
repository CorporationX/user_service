package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.UserResponseDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "promotions", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotions", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    UserResponseDto toUserResponseDto(User user);

    @Named("mapTariff")
    default String mapTariff(List<UserPromotion> userPromotions) {
        Optional<UserPromotion> promotionOpt = getActivePromotion(userPromotions);
        return promotionOpt
                .map(userPromotion -> userPromotion.getPromotionTariff().toString())
                .orElse(null);
    }

    @Named("mapNumberOfViews")
    default Integer mapNumberOfViews(List<UserPromotion> userPromotions) {
        Optional<UserPromotion> promotionOpt = getActivePromotion(userPromotions);
        return promotionOpt
                .map(UserPromotion::getNumberOfViews)
                .orElse(null);
    }

    default Optional<UserPromotion> getActivePromotion(List<UserPromotion> userPromotions) {
        return userPromotions
                .stream()
                .filter(promotion -> promotion.getNumberOfViews() > 0)
                .findFirst();
    }
}
