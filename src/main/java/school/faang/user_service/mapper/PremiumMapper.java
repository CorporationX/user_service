package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {

    @Mapping(target = "userId", source = "user.id")
    PremiumDto toDto(Premium premium);

    @Mapping(target = "user", ignore = true)
    Premium toEntity(PremiumDto premiumDto);
}
