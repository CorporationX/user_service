package school.faang.user_service.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserResponseDto toDto(User entity);

    List<UserResponseDto> toDtos(List<User> entities);

    @Mapping(source = "countryId", target = "country.id")
    User toEntity(UserDto userDto);

    @Mapping(source = "country.id", target = "countryId")
    UserDto toUserDto(User user);
}
