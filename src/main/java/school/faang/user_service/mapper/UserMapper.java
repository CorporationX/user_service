package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "preferredContact", source = "contactPreference.preference")
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "followers", target = "subscriberIds", qualifiedByName = "getIdFromUser")
    UserDto toDto(User user);

    @Mapping(target = "contactPreference.preference", source = "preferredContact")
    @Mapping(source = "subscriberIds", target = "followers", qualifiedByName = "getUserFromId")
    User toEntity(UserDto userDto);

    @Named("getIdFromUser")
    default Long getIdFromUser(User user) {
        return user.getId();
    }

    @Named("getUserFromId")
    default User getUserFromId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
