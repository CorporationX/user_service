package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PromotionMapper {
    @Mapping(source = "user.id", target = "userId")
    PromotionDto toDto(Promotion promotion);

    @Mapping(source = "userId", target = "user", qualifiedByName = "mapIdToUser")
    Promotion toEntity(PromotionDto dto);

    @Named("mapIdToUser")
    default User mapIdToUser(Long id) {
        User user = new User();
        user.setId(id);

        return user;
    }
}
