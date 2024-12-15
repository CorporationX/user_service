package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserContactsDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.ContactPreference;
import school.faang.user_service.entity.contact.PreferredContact;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserContactsMapper {

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserContactsDto toDto(User user);

    default PreferredContact mapPreference(ContactPreference contactPreference) {
        return contactPreference != null ? contactPreference.getPreference() : null;
    }
}
