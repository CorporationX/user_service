package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.model.premium.Premium;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    PremiumDto toDto(Premium premium);

    @Mapping(target = "user", ignore = true)
    Premium toEntity(PremiumDto premiumDto);

    List<PremiumDto> toDto(List<Premium> premiums);
    List<Premium> toEntity(List<PremiumDto> premiumDtos);
}
