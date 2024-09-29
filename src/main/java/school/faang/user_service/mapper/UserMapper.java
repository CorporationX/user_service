package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperUtil;
import school.faang.user_service.pojo.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapperUtil.class})
public interface UserMapper {
    SubscriptionUserDto toSubscriptionUserDto(User user);

    List<SubscriptionUserDto> toSubscriptionUserDtoList(List<User> users);

    UserDto userToUserDto(User user);

    MentorshipUserDto toMentorshipUserDto(User user);

    User dtoUserToUser(UserDto userDto);

    List<MentorshipUserDto> toMentorshipUserDtos(List<User> users);

    List<User> userDtosToUsers(List<UserDto> userDtos);

    List<UserDto> usersToUserDtos(List<User> users);

    List<SubscriptionUserDto> toSubscriptionUserDtos(List<User> users);

    @Mapping(target = "username", source = "person", qualifiedByName = {"UserMapperUtil", "getUserName"})
    @Mapping(target = "password", source = "person.firstName", qualifiedByName = {"UserMapperUtil", "getGeneratedPassword"})
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = {"UserMapperUtil", "getAboutMe"})
    User toUserFromPerson(Person person);
}