package school.faang.user_service.mapper;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.register.UserProfileDto;
import school.faang.user_service.dto.register.UserRegistrationDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "registrationDto.username", target = "username")
    @Mapping(source = "registrationDto.email", target = "email")
    @Mapping(source = "registrationDto.password", target = "password")
    User userRegistrationDtoToUser(UserRegistrationDto registrationDto);

    @Mapping(source = "registrationDto.username", target = "username")
    @Mapping(source = "registrationDto.email", target = "email")
    @Mapping(source = "registrationDto.password", target = "password")
    User userRegistrationDtoToUserWithCountry(UserRegistrationDto registrationDto, @Context Country country);

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    UserProfileDto userToUserProfileDto(User user);

    UserDto userToUserDto(User user);

    UserDto toDto(User user);
}
