package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.CountryDto;
import school.faang.user_service.entity.Country;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CountryMapper {
    Country toEntity(CountryDto countryDto);
    CountryDto toDto(Country country);
}
