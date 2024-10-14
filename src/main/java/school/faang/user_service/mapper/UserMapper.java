package school.faang.user_service.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.PersonDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ContactInfoMapper.class, AddressMapper.class, EducationMapper.class})
public interface UserMapper {
    @Mapping(source = "contactPreference.preference", target = "preference")
    @Mapping(source = "user.telegramContact.telegramUserId", target = "telegramUserId")
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
    List<UserDto> toListUserDto(List<User> users);

    PersonDto toPersonDto(User user);
    User toEntity(PersonDto personDto);

    @AfterMapping
    default PersonDto mapUsernameToPerson(@MappingTarget PersonDto personDto, User user) {
        String[] names = user.getUsername().split(" ");

        List<String> nameList = Arrays.stream(names)
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toList());

        return new PersonDto(
                nameList.stream().findFirst().orElse(""), // firstName
                nameList.size() > 1 ? nameList.get(1) : "", // lastName
                personDto.contactInfo(),
                personDto.education(),
                personDto.employer(),
                personDto.yearOfBirth(),
                personDto.group(),
                personDto.studentID()
        );
    }
}