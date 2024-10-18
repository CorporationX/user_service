package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.dto.user.UserResponseShortDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.UserPromotion;

import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserResponseDto toDto(User entity);

    List<UserResponseDto> toDtos(List<User> entities);

    UserResponseShortDto toUserResponseShortDto(User user);
    List<UserResponseShortDto> toUserResponseShortDtos(List<User> users);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserDto userDto);

    @Mapping(source = "country.id", target = "countryId")
    UserDto toUserDto(User user);

    @Mapping(source = "promotions", target = "promotionTariff", qualifiedByName = "mapTariff")
    @Mapping(source = "promotions", target = "numberOfViews", qualifiedByName = "mapNumberOfViews")
    school.faang.user_service.dto.promotion.UserResponseDto toUserResponseDto(User user);

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
