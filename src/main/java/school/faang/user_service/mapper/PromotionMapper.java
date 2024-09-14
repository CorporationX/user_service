package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PromotionMapper {
    @Mapping(source = "user.id", target = "promotedUserId")
    PromotionDto toPromotionDto(Promotion promotion);
}
