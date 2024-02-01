package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.entity.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "followers", target = "followersIds")
    @Mapping(source = "followees", target = "followeesIds")
    @Mapping(source = "ownedEvents", target = "ownedEventsIds")
    @Mapping(source = "mentees", target = "menteesIds")
    @Mapping(source = "mentors", target = "mentorsIds")
    @Mapping(source = "skills", target = "skillsIds")
    UserDto toUserDto(User user);

    @Mapping(source = "countryId", target = "country")
    @Mapping(source = "followersIds", target = "followers")
    @Mapping(source = "followeesIds", target = "followees")
    @Mapping(source = "ownedEventsIds", target = "ownedEvents")
    @Mapping(source = "menteesIds", target = "mentees")
    @Mapping(source = "mentorsIds", target = "mentors")
    @Mapping(source = "skillsIds", target = "skills")
    User toUser(UserDto userDTO);

    List<UserDto> toUserDtoList(List<User> users);
    List<User> toUserList(List<UserDto> userDtoList);

    List<User> mapUsersIdsToUsers(List<Long> usersIds);

    List<Event> mapEventsIdsToEvents(List<Long> eventsIds);

    List<Skill> mapSkillsIdsToSkills(List<Long> skillsIds);

    List<Long> mapUsersToUsersIds(List<User> users);

    List<Long> mapEventsToEventsIds(List<Event> events);

    List<Long> mapSkillsToSkillsIds(List<Skill> skills);

    default Country mapIdToCountry(Long id) {
        if (id == null) {
            return null;
        }
        return Country.builder()
                .id(id)
                .build();
    }

    default User mapIdToUser(Long id) {
        if (id == null) {
            return null;
        }
        return User.builder()
                .id(id)
                .build();
    }

    default Event mapIdToEvent(Long id) {
        if (id == null) {
            return null;
        }
        return Event.builder()
                .id(id)
                .build();
    }

    default Skill mapIdToSkill(Long id) {
        if (id == null) {
            return null;
        }
        return Skill.builder()
                .id(id)
                .build();
    }

    default Long mapUserToId(User user) {
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    default Long mapEventToId(Event event) {
        if (event == null) {
            return null;
        }
        return event.getId();
    }

    default Long mapSkillToId(Skill skill) {
        if (skill == null) {
            return null;
        }
        return skill.getId();
    }

}
