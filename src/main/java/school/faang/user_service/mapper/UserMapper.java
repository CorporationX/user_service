package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserRegistrationDto;
import school.faang.user_service.dto.UserSubResponseDto;
import school.faang.user_service.dto.user.DeactivatedUserDto;
import school.faang.user_service.dto.user.MenteeResponseDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserForNotificationDto;
import school.faang.user_service.dto.user.UserProfileCreateDto;
import school.faang.user_service.dto.user.UserProfileResponseDto;
import school.faang.user_service.dto.user.UserSearchResponse;
import school.faang.user_service.message.event.reindex.user.UserDocument;
import school.faang.user_service.model.Country;
import school.faang.user_service.model.Skill;
import school.faang.user_service.model.User;
import school.faang.user_service.model.event.Event;
import school.faang.user_service.model.event.Rating;
import school.faang.user_service.model.goal.Goal;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {GoalMapper.class, EventMapper.class, SkillMapper.class, CountryMapper.class},
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserDto> toDto(List<User> users);

    /*@Mapping(source = "id", target = "userId")
    @Mapping(source = "country.title", target = "country")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "toAverageRating")
    UserSearchResponse toSearchResponse(User user);*/

    List<UserSearchResponse> toResponseList(List<User> userDocuments);

    @Mapping(source = "id", target = "userId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    UserSearchResponse toSearchResponse(User user);

    List<UserSearchResponse> toSearchResponseList(List<User> users);

    @Mapping(source = "id", target = "resourceId")
    @Mapping(source = "country", target = "country", qualifiedByName = "toCountryName")
    @Mapping(source = "skills", target = "skillNames",
            qualifiedByName = "toSkillNameList")
    @Mapping(source = "ratings", target = "averageRating", qualifiedByName = "toAverageRating")
    UserDocument toUserDocument(User user);

    List<UserDocument> toUserDocumentList(List<User> users);

    UserProfileResponseDto toUserProfileResponseDto(User user);

    @Mapping(target = "country", ignore = true)
    User toEntity(UserProfileCreateDto dto);

    @Named("toAverageRating")
    default Double toAverageRating(List<Rating> ratings) {
        return ratings.stream()
                .map(Rating::getRate)
                .mapToDouble(Double::valueOf)
                .average()
                .orElse(0);
    }

    @Named("toCountryName")
    default String toCountryName(Country country) {
        return country.getTitle();
    }

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

    UserDto toDto(User user);

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

    @Mapping(target = "country", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(UserRegistrationDto userRegistrationDto);

    List<UserDto> toDtos(List<User> users);
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