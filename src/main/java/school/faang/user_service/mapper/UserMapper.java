package school.faang.user_service.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserProfilePic;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.entity.contact.ContactType;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(target = "telegramChatId", source = "contacts", qualifiedByName = "getTelegramContact")
    @Mapping(target = "followerIds", source = "followers", qualifiedByName = "mapUserListToIdList")
    @Mapping(target = "followeeIds", source = "followees", qualifiedByName = "mapUserListToIdList")
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

    @Named("mapUserListToIdList")
    default List<Long> mapUserListToIdList(List<User> followers) {
        if (followers == null) {
            return null;
        }
        return followers.stream()
                .map(user -> user.getId())
                .toList();
    }

    @Named("mapUserPicToString")
    default String mapUserPicToString(UserProfilePic profilePic) {
        return profilePic.getSmallFileId();
    }
}