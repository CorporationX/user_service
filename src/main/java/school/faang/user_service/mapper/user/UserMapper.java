package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "contactPreference", target = "preferredContact", qualifiedByName = "contactPreference_to_preferredContact")
    @Mapping(source = "goals", target = "goalIds", qualifiedByName = "convertGoalToId")
    @Mapping(source = "contactPreference", target = "contactPreference", qualifiedByName = "contactPreference_to_preferredContact")
    UserDto toDto(User user);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);

    @Named("contactPreference_to_preferredContact")
    default PreferredContact convertUserToId(ContactPreference contactPreference) {
        if (contactPreference != null) {
            return contactPreference.getPreference();
        } else
            return null;
    }

    @Named("convertGoalToId")
    default List<Long> convertGoalToId(List<Goal> goals) {
        return goals.stream().map(Goal::getId).collect(Collectors.toList());
    }

    @Named("contactPreference_to_preferredContact")
    default PreferredContact convertUserToId(ContactPreference contactPreference) {
        if (contactPreference != null) {
            return contactPreference.getPreference();
        } else
            return null;
    }
}