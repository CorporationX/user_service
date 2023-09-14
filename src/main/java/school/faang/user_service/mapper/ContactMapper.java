package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.ContactDto;
import school.faang.user_service.entity.contact.Contact;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactMapper {

    @Mapping(target = "userId", source = "user.id")
    ContactDto toDto(Contact contact);

    @Mapping(target = "user.id", source = "userId")
    Contact toEntity(ContactDto contactDto);
}
