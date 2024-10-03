package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.subscription.SubscriptionUserDto;
import school.faang.user_service.dto.MentorshipUserDto;
import school.faang.user_service.dto.user.UserDtoForRegistration;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
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

    @Mapping(source = "country.id", target = "countryId")
    UserDtoForRegistration toUserDtoForRegistration(User user);

    @Mapping(source = "countryId", target = "country", qualifiedByName = "mapToCountry")
    User toUser(UserDtoForRegistration dto);

    @Named("mapToCountry")
    default Country mapToCountry(long countryId) {
        return Country.builder().id(countryId).build();
    }
}