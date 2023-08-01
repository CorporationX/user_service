package school.faang.user_service.mapper.mymappers;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mydto.CountryDto;
import school.faang.user_service.entity.Country;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface Country1Mapper {

    CountryDto toDto(Country country);

    Country toEntity(CountryDto countryDto);
}
