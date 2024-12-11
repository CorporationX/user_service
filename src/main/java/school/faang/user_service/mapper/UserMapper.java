package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.ParticipantDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.dto.user.UserForNotificationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    MenteeResponseDto toMenteeResponseDto(User user);

    List<MenteeResponseDto> toMenteeResponseList(List<User> users);

    UserSubResponseDto toUserSubResponseDto(User user);

    List<UserSubResponseDto> toUserSubResponseList(List<User> users);

    List<User> menteeResponsesToUserList(List<MenteeResponseDto> menteeResponseDtos);

    @Mapping(source = "settingGoals", target = "idsSettingGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "goals", target = "idsGoals", qualifiedByName = "mapGoalsToListId")
    @Mapping(source = "skills", target = "idsSkills", qualifiedByName = "mapSkillsToListId")
    @Mapping(source = "mentors", target = "idsMentors", qualifiedByName = "mapMentorsToListId")
    @Mapping(source = "ownedEvents", target = "idsOwnedEvents", qualifiedByName = "mapEventsToListId")
    @Mapping(source = "participatedEvents", target = "idsParticipatedEvent", qualifiedByName = "mapEventsToListId")
    @Mapping(source = "country.id", target = "countryId")
    DeactivatedUserDto toDeactivatedUserDto(User user);

    @Mapping(target = "settingGoals", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)

    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "country", ignore = true)
    User deactivatedUserDtoToEntity(DeactivatedUserDto deactivatedUserDto);

    UserDto toUserDto(User user);

    @Mapping(target = "preference", source = "contactPreference.preference")
    UserForNotificationDto toUserForNotificationDto(User user);

    @Named("mapGoalsToListId")
    default List<Long> mapGoalsToListId(List<Goal> goals) {
        if (goals == null) {
            return new ArrayList<>();
        }
        return goals.stream()
                .map(Goal::getId)
                .toList();
    }
    User toEntity(UserDto userDto);

    List<ParticipantDto> toDtos(List<User> users);

    @Named("mapSkillsToListId")
    default List<Long> mapSkillsToListId(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .toList();
    }

    List<User> toEntities(List<UserDto> userDtos);
    @Named("mapMentorsToListId")
    default List<Long> mapMentorsToListId(List<User> mentors) {
        if (mentors == null) {
            return new ArrayList<>();
        }
        return mentors.stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapEventsToListId")
    default List<Long> mapEventsToListId(List<Event> ownedEvents) {
        if (ownedEvents == null) {
            return new ArrayList<>();
        }
        return ownedEvents.stream()
                .map(Event::getId)
                .toList();
    }
}