package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
