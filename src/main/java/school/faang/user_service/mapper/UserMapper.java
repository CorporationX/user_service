package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(target = "telegramChatId", source = "contacts", qualifiedByName = "getTelegramContact")
    @Mapping(target = "followerIds", source = "followers", qualifiedByName = "getUserFollowerIds")
    @Mapping(target = "followeeIds", source = "followees", qualifiedByName = "getUserFolloweeIds")
    @Mapping(target = "smallFileId", source = "userProfilePic.smallFileId")
    UserDto toDto(User user);

    @Named("getTelegramContact")
    default String getTelegramContact(List<Contact> contacts) {
        if (contacts == null) {
            return null;
        }
        return contacts.stream()
                .filter(contact -> contact.getType() == ContactType.TELEGRAM)
                .map(Contact::getContact)
                .findFirst().orElse(null);
    }

    @Named("getUserFollowerIds")
    default List<Long> getUserFollowerIds(List<User> followers) {
        return followers == null ? new ArrayList<>() : followers.stream()
                .map(User::getId)
                .toList();
    }

    @Named("getUserFolloweeIds")
    default List<Long> getUserFolloweeIds(List<User> followees) {
        return followees == null ? new ArrayList<>() : followees.stream()
                .map(User::getId)
                .toList();
    }
}