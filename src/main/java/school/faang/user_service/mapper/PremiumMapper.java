package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.premium.PremiumDto;
import school.faang.user_service.entity.premium.Premium;

import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PremiumMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "days", expression = "java(calculateDays(premium))")
    PremiumDto toDto(Premium premium);

    @Mapping(source = "userId", target = "user.id")
    Premium toEntity(PremiumDto premiumDto);

    default int calculateDays(Premium premium) {
        return (int) ChronoUnit.DAYS.between(premium.getStartDate(), premium.getEndDate());
    }
}
