package school.faang.user_service.mapper.promotion;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.promotion.PromotionDto;
import school.faang.user_service.entity.promotion.PromotionTariff;

import java.util.List;

/**
 * @author Evgenii Malkov
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper {

    @Mapping(source = "promotionType.id", target = "typeId")
    @Mapping(source = "promotionType.description", target = "typeDescription")
    PromotionDto tariffToPromotionDto(PromotionTariff tariff);

    List<PromotionDto> tariffToPromotionDtoList(List<PromotionTariff> tariffs);
}
