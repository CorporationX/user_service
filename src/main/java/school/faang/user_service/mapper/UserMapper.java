package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "mapToIds")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "mapToIds")
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(source = "countryId", target = "country")
    @Mapping(target = "contactPreference", source = "preferredContact")
    User toEntity(UserDto userDto);
    @Named("mapToIds")
    default List<Long> mapToIds(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream().map(User::getId).toList();
    }

    default Country map(Long countryId) {
        Country country = new Country();
        country.setId(countryId);
        return country;
    }

}
