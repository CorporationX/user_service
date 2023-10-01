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

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    User toEntity(UserDto userDto);

    @Mapping(target = "preference", source = "contactPreference.preference")
    @Mapping(target = "telegramChatId", source = "contacts", qualifiedByName = "getTelegramContact")
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
}