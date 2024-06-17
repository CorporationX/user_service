package school.faang.user_service.mapper.contact;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.contact.ContactPreference;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ContactPreferenceMapper {

    @Mapping(source = "userId", target = "user.id")
    ContactPreference toEntity(ContactPreferenceDto contactPreferenceDto);

    @Mapping(source = "user.id", target = "userId")
    ContactPreferenceDto toDto(ContactPreference contact);
}
