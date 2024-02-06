package school.faang.user_service.mapper.user;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    UserDto toUserDto(User user);

    @Mapping(source = "countryId", target = "country")
    User toUser(UserDto userDTO);

    default Country mapIdToCountry(Long id) {
        if (id == null) {
            return null;
        }
        return Country.builder()
                .id(id)
                .build();
    }

}
