package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserCacheDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user_profile.UserProfileSettingsResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesId", qualifiedByName = "mapToUsersId")
    @Mapping(source = "mentors", target = "mentorsId", qualifiedByName = "mapToUsersId")
    @Mapping(source = "skills", target = "skillsId", qualifiedByName = "mapToSkillsId")
    UserDto toDto(User user);

    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "skills", ignore = true)
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);

    List<User> toEntity(List<UserDto> usersDto);

    @Mapping(target = "userId", source = "user.id")
    UserProfileSettingsResponseDto toDto(ContactPreference contactPreference);

    @Named("mapToUsersId")
    default List<Long> mapToUsersId(List<User> users) {
        return mapToIds(users, User::getId);
    }

    @Mapping(source = "followees", target = "followeesIds", qualifiedByName = "mapToUsersId")
    UserCacheDto toCacheDto(User user);

    @Named("mapToSkillsId")
    default List<Long> mapToSkillsId(List<Skill> skills) {
        return mapToIds(skills, Skill::getId);
    }

    default <T> List<Long> mapToIds(List<T> items, Function<T, Long> mapper) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream().map(mapper).toList();
    }
}
