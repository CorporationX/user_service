package school.faang.user_service.mapper.contact;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.contact.ContactPreferenceDto;
import school.faang.user_service.entity.contact.ContactPreference;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ContactPreferenceMapper {
    ContactPreference toEntity(ContactPreferenceDto dto);

    ContactPreferenceDto toDto(ContactPreference contact);
}
