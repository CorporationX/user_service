package school.faang.user_service.mapper.contact;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.contact.ContactDto;
import school.faang.user_service.entity.contact.Contact;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContactMapper {
    Contact toEntity(ContactDto dto);

    ContactDto toDto(Contact contact);
}
