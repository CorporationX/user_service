package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PromotionMapper {
    @Mapping(source = "user.id", target = "userId")
    PromotionDto toPromotionDto(Promotion promotion);

    @Mapping(target = "user", ignore = true)
    Promotion toEntity(PromotionDto dto);
}
