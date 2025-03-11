package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(target = "followersIds", expression = "java(mapIds(user.getFollowers()))")
    @Mapping(target = "followeesIds", expression = "java(mapIds(user.getFollowees()))")
    @Mapping(target = "menteesIds", expression = "java(mapIds(user.getMentees()))")
    @Mapping(target = "mentorsIds", expression = "java(mapIds(user.getMentors()))")
    UserDto toDto(User user);

    @InheritInverseConfiguration(name = "toDto")
    @Mapping(target = "userProfilePic", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "sentMentorshipRequests", ignore = true)
    @Mapping(target = "sentGoalInvitations", ignore = true)
    @Mapping(target = "recommendationsReceived", ignore = true)
    @Mapping(target = "recommendationsGiven", ignore = true)
    @Mapping(target = "receivedMentorshipRequests", ignore = true)
    @Mapping(target = "receivedGoalInvitations", ignore = true)
    @Mapping(target = "ratings", ignore = true)
    @Mapping(target = "premium", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "participatedEvents", ignore = true)
    @Mapping(target = "ownedEvents", ignore = true)
    @Mapping(target = "mentors", ignore = true)
    @Mapping(target = "mentees", ignore = true)
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "followers", ignore = true)
    @Mapping(target = "followees", ignore = true)
    @Mapping(target = "country", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "contactPreference", ignore = true)
    User toEntity(UserDto userDto);

    default List<Long> mapIds(List<User> users) {
        return users == null ? null : users.stream()
                .map(User::getId)
                .toList();
    }

}

