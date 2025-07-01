package school.faang.user_service.mapper.promotion;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromotionEventCreateRequestDto;
import school.faang.user_service.dto.promotion.PromotionResponseDto;
import school.faang.user_service.entity.promotion.Promotion;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PromotionEntityMapper {
    Promotion toPromotionEntity(PromotionEventCreateRequestDto promotionEventCreateRequestDto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "event.id", target = "eventId")
    @Mapping(source = "tariff.id", target = "tariffId")
    PromotionResponseDto toPromotionResponseDto(Promotion promotion);
}
