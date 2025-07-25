package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.user.CreateUserDto;
import school.faang.user_service.dto.user.UpdateUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.kafka.dto.user.UserCreated;
import school.faang.user_service.kafka.dto.user.update.UserUpdate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {

    User toUser(CreateUserDto userDto);

    void update(UpdateUserDto userDto, @MappingTarget User entity);

    UserDto toUserDto(User user);

    @Mapping(source = "country.id", target = "countryId")
    UserUpdate toUserUpdatedDto(User user);

    UserCreated toUserCreatedDto(User user);
}
