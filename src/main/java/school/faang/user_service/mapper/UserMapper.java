package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserTransportDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "country")
    UserDto toDto(User user);

    @Mapping(source = "country", target = "country.id")
    User toEntity(UserDto userDto);

    UserTransportDto toTransportDto(User user);

    @Mapping(source = "active", target = "isActive")
    List<UserDto> toDtoList(List<User> users);
}
