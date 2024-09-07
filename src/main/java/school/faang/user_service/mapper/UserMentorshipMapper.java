package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.mentorship.UserMentorshipDto;
import school.faang.user_service.entity.User;

import java.util.Collection;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMentorshipMapper {
    @Mapping(source = "country.id", target = "countryId")
    UserMentorshipDto toDto(User user);

    Collection<UserMentorshipDto> toDtoList(Collection<User> users);
}
