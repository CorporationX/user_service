package school.faang.user_service.mapper.premium;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.premium.ResponsePremiumDto;
import school.faang.user_service.service.premium.util.Premium;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResponsePremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    ResponsePremiumDto toDto(Premium premium);
}
