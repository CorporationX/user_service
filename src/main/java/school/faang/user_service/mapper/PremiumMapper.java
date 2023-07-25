package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PremiumMapper {

    PremiumDto toDto(Premium premium);

    Premium toEntity(PremiumDto premiumDto);
}
