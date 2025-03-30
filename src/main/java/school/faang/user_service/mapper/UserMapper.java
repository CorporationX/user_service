package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.promotion.PromoUserDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.pojo.Person;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MentorshipRequestMapper.class)
public interface UserMapper {

    @Mapping(source = "contactPreference.preference", target = "preference")
    UserResponseDto toUserResponseDto(User user);

    @Mapping(source = "user.id", target = "userId")
    UserDto toUserDto(User user);

    @Mapping(target = "username", expression = "java(getUsername(person))")
    @Mapping(target = "country", expression = "java(getCountry(person))")
    @Mapping(target = "aboutMe", expression = "java(person.toString())")
    User toUserEntity(Person person);

    default String getUsername(Person person) {
        return person.getFirstName() + person.getLastName();
    }

    default Country getCountry(Person person) {
        return Country.builder().title(person.getCountry()).build();
    }

    @Mapping(target = "title", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "country", source = "country.title")
    PromoUserDto toPromoUserDto(User user);
}
