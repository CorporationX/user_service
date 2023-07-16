package school.faang.user_service.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mappings({
            @Mapping(source = "country.title", target = "country"),
            @Mapping(source = "followers", target = "followerIds", qualifiedByName = "mapUserIds"),
            @Mapping(source = "followees", target = "followeeIds", qualifiedByName = "mapUserIds"),
            @Mapping(source = "mentees", target = "menteeIds", qualifiedByName = "mapUserIds"),
            @Mapping(source = "mentors", target = "mentorIds", qualifiedByName = "mapUserIds"),
            @Mapping(source = "goals", target = "goalIds", qualifiedByName = "mapGoalIds"),
            @Mapping(source = "skills", target = "skillIds", qualifiedByName = "mapSkillIds"),
            @Mapping(source = "recommendationsGiven",
                    target = "recommendationGivenIds",
                    qualifiedByName = "mapRecommendationIds"),
            @Mapping(source = "recommendationsReceived",
                    target = "recommendationReceivedIds",
                    qualifiedByName = "mapRecommendationIds")
    })
    UserDto userToUserDto(User user);

    @Mappings({
            @Mapping(target = "country", ignore = true),
            @Mapping(target = "followers", ignore = true),
            @Mapping(target = "followees", ignore = true),
            @Mapping(target = "mentees", ignore = true),
            @Mapping(target = "mentors", ignore = true),
            @Mapping(target = "goals", ignore = true),
            @Mapping(target = "skills", ignore = true),
            @Mapping(target = "recommendationsGiven", ignore = true),
            @Mapping(target = "recommendationsReceived", ignore = true)
    })
    User userDtoToUser(UserDto userDto);

    @Named("mapUserIds")
    default List<Long> mapUserIds(List<User> users) {
        return users.stream()
                .map(User::getId)
                .toList();
    }

    @Named("mapGoalIds")
    default List<Long> mapGoalIds(List<Goal> followers) {
        return followers.stream()
                .map(Goal::getId)
                .toList();
    }

    @Named("mapSkillIds")
    default List<Long> mapSkillIds(List<Skill> followers) {
        return followers.stream()
                .map(Skill::getId)
                .toList();
    }

    @Named("mapRecommendationIds")
    default List<Long> mapRecommendationIds(List<Recommendation> followers) {
        return followers.stream()
                .map(Recommendation::getId)
                .toList();
    }
}
