package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "preferredContact", target = "contactPreference.preference")
    User toEntity(UserDto userDto);

    @Mapping(source = "contactPreference.preference", target = "preferredContact")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    default void obfuscateUser(User user) {
        user.setActive(false);
        user.setPhone(null);
        user.setAboutMe(null);
        user.setCity(null);
        user.setExperience(null);
        user.setUserProfilePic(null);
        user.getSetGoals().clear();
    }
}