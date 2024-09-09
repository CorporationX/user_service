package school.faang.user_service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toDto(User entity);

    List<UserResponseDto> toDtos(List<User> entity);
}
