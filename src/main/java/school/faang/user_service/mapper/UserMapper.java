package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserDtoForRegistration;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapperUtil;
import school.faang.user_service.pojo.Person;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapperUtil.class})
public interface UserMapper {
    SubscriptionUserDto toSubscriptionUserDto(User user);

    List<SubscriptionUserDto> toSubscriptionUserDtoList(List<User> users);

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserDto userToUserDto(User user);

    MentorshipUserDto toMentorshipUserDto(User user);

    User dtoUserToUser(UserDto userDto);

    List<MentorshipUserDto> toMentorshipUserDtos(List<User> users);

    List<User> userDtosToUsers(List<UserDto> userDtos);

    List<UserDto> usersToUserDtos(List<User> users);

    List<SubscriptionUserDto> toSubscriptionUserDtos(List<User> users);

    @Mapping(target = "username", source = "person", qualifiedByName = {"UserMapperUtil", "getUserName"})
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "country.title", source = "contactInfo.address.country")
    @Mapping(target = "aboutMe", source = "person", qualifiedByName = {"UserMapperUtil", "getAboutMe"})
    User toUserFromPerson(Person person);

    @Mapping(source = "country.id", target = "countryId")
    UserDtoForRegistration toUserDtoForRegistration(User user);

    @Mapping(source = "countryId", target = "country", qualifiedByName = "mapToCountry")
    User toUser(UserDtoForRegistration dto);

    @Named("mapToCountry")
    default Country mapToCountry(long countryId) {
        return Country.builder().id(countryId).build();
    }
}