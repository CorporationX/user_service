package school.faang.user_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.entity.contact.Contact;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactMapper {
    ContactDto contactToContactDto(Contact contact);
    Contact contactDtoToContact(ContactDto contactDto);

    List<ContactDto> contactsToContactDtos(List<Contact> contacts);
    List<Contact> contactDtosToContacts(List<ContactDto> contactDtos);
}
