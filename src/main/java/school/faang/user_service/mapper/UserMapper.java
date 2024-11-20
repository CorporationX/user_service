package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Skill;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    UserDto toUserDto(User user);

    @Mapping(target = "country", ignore = true)
    User toUser(UserDto userDto);

    List<UserDto> toDto(List<User> users);

    List<User> toEntity(List<UserDto> userDtos)

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
    User toEntity(DeactivatedUserDto deactivatedUserDto);

    @Named("mapGoalsToListId")
    default List<Long> mapGoalsToListId(List<Goal> goals) {
        if (goals == null) {
            return new ArrayList<>();
        }
        return goals.stream()
                .map(goal -> goal.getId())
                .toList();
    }

    @Named("mapSkillsToListId")
    default List<Long> mapSkillsToListId(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(skill -> skill.getId())
                .toList();
    }

    @Named("mapMentorsToListId")
    default List<Long> mapMentorsToListId(List<User> mentors) {
        if (mentors == null) {
            return new ArrayList<>();
        }
        return mentors.stream()
                .map(mentor -> mentor.getId())
                .toList();
    }

    @Named("mapEventsToListId")
    default List<Long> mapEventsToListId(List<Event> ownedEvents) {
        if (ownedEvents == null) {
            return new ArrayList<>();
        }
        return ownedEvents.stream()
                .map(event -> event.getId())
                .toList();
    }
}