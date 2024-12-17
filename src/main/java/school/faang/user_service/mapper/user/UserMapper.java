package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.document.UserDocument;
import school.faang.user_service.entity.goal.Goal;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "mentees", target = "menteesIds", qualifiedByName = "mentees")
    @Mapping(source = "mentors", target = "mentorsIds", qualifiedByName = "mentors")
    @Mapping(source = "followers", target = "followersIds", qualifiedByName = "followers")
    @Mapping(source = "followees", target = "followeesIds", qualifiedByName = "followees")
    @Mapping(source = "goals", target = "goalsIds", qualifiedByName = "goals")
    @Mapping(source = "skills", target = "skillsIds", qualifiedByName = "skills")
    @Mapping(source = "country.title", target = "country")
    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);

    @Mapping(target = "country", ignore = true)
    User toEntity(UserDto dto);

    @Mapping(source = "country.title", target = "country")
    @Mapping(source = "skills", target = "skills", qualifiedByName = "skillToTitle")
    UserDocument userToUserDocument(User user);

    @Named("skillToTitle")
    default List<String> mapSkillsToTitle(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getTitle)
                .toList();
    }

    @Named("mentees")
    default List<Long> getMenteesIds(List<User> mentees) {
        if (mentees == null) {
            return new ArrayList<>();
        }
        return mentees.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("followers")
    default List<Long> getFollowersIds(List<User> followers) {
        if (followers == null) {
            return new ArrayList<>();
        }
        return followers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("followees")
    default List<Long> getFolloweesIds(List<User> followees) {
        if (followees == null) {
            return new ArrayList<>();
        }
        return followees.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("setGoals")
    default List<Long> getSetGoalsIds(List<Goal> setGoals) {
        if (setGoals == null) {
            return new ArrayList<>();
        }
        return setGoals.stream()
                .map(Goal::getId)
                .collect(Collectors.toList());
    }

    @Named("goals")
    default List<Long> getGoalsIds(List<Goal> goals) {
        if (goals == null) {
            return new ArrayList<>();
        }
        return goals.stream()
                .map(Goal::getId)
                .collect(Collectors.toList());
    }

    @Named("skills")
    default List<Long> getSkillsIds(List<Skill> skills) {
        if (skills == null) {
            return new ArrayList<>();
        }
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Named("mentors")
    default List<Long> getMentorsIds(List<User> mentors) {
        if (mentors == null) {
            return new ArrayList<>();
        }
        return mentors.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
