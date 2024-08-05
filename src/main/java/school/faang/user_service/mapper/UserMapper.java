package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserCreateDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target= "country.id", source = "countryId")
    User toEntity(UserCreateDto userDto);

    UserDto toDto(User user);

    @Mapping(target = "countryId", source = "country.id")
    UserCreateDto toDtoCreated(User user);

    List<UserDto> toDtoList(List<User> users);
}
