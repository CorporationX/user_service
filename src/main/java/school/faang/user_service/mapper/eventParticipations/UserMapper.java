package school.faang.user_service.mapper.eventParticipations;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper{
    UserDto toDto(User entity);
    User toEntity(UserDto dto);
}
