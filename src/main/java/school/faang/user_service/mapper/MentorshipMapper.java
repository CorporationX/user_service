package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {
    @Mapping(source = "country", target = "country", qualifiedByName = "mapToString")
    MentorshipDto toDto(User user);

    @Named(value = "mapToString")
    default String mapCountry(Country country) {
        return country.getTitle();
    }
}
