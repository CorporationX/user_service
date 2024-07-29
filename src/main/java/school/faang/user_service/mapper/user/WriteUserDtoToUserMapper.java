package school.faang.user_service.mapper.user;

import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import school.faang.user_service.dto.user.WriteUserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;

@Slf4j
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class WriteUserDtoToUserMapper {

    @Autowired
    protected CountryRepository countryRepository;

    @Mapping(target = "country", expression = "java(findCountryById(writeUserDto.getCountryId()))")
    public abstract User map(WriteUserDto writeUserDto);

    protected Country findCountryById(Long id) {
        return countryRepository.findById(id).orElseThrow(() -> {
            log.error(String.format("WriteUserDtoToUserMapper.findCountryById: Country with id %s not found", id));
            return new IllegalArgumentException("Country with id " + id + " not found");
        });
    }
}