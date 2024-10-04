package school.faang.user_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.PersonDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ContactInfoMapper.class, AddressMapper.class, EducationMapper.class})
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toListUserDto(List<User> users);

    PersonDto toPersonDto(User user);
    User toEntity(PersonDto personDto);

    @AfterMapping
    default void mapUsernameToPerson(@MappingTarget PersonDto personDto, User user) {
        String[] names = user.getUsername().split(" ");
        personDto.setFirstName(names[0]);
        personDto.setLastName(names.length > 1 ? names[1] : "");
    }

    @AfterMapping
    default void mapPersonToUsername(@MappingTarget User user, PersonDto personDto) {
        user.setUsername(personDto.getFirstName() + " " + personDto.getLastName());
    }
}