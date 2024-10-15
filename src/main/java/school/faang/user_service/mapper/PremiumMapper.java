package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.model.dto.PremiumDto;
import school.faang.user_service.model.entity.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toPremiumDto(Premium premium);
}
