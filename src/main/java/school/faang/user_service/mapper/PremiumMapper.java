package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SkillOfferMapper.class})
public interface PremiumMapper {
    PremiumDto toDto(Premium premium);

    Premium toEntity(PremiumDto dto);
}
