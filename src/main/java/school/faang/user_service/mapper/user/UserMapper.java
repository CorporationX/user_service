package school.faang.user_service.mapper.user;

import org.mapstruct.InjectionStrategy;
import com.json.student.PersonSchemaV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserCreateDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "country.id", target = "countryId")
    @Mapping( source = "contactPreference.preference", target = "preference")
    UserDto toDto(User user);

    @Mapping(source = "country.id", target = "countryId")
    @Mapping(source = "userProfilePic.fileId", target = "userProfilePic")
    UserCreateDto toUserCreateDto(User user);

    @Mapping(source = "countryId", target = "country.id")
    @Mapping(source = "userProfilePic", target = "userProfilePic.fileId")
    User toEntity(UserCreateDto userCreateDto);

    List<UserDto> toUserDtoList(List<User> users);

    @Mapping(source = "countryId", target = "country")
    User toUser(UserDto userDTO);

    default Country mapIdToCountry(Long id) {
        if (id == null) {
            return null;
        }
        return Country.builder()
                .id(id)
                .build();
    }

    @Mapping(target = "username", expression = "java(person.getFirstName() + \"_\" + person.getLastName())")
    @Mapping(target = "aboutMe", source = ".", qualifiedByName = "aboutMe")
    @Mapping(target = "country.title", source = "country")
    User personToUser(PersonSchemaV2 person);

    @Named("aboutMe")
    default String getAboutMe(PersonSchemaV2 person) {
        StringBuilder builder = new StringBuilder();
        if (person != null) {
            if (person.getState() != null) {
                builder.append("State - " + person.getState()).append(", ");
            }
            if (person.getFaculty() != null) {
                builder.append("Faculty - " + person.getFaculty()).append(", ");
            }
            if (person.getYearOfStudy() != null) {
                builder.append("Year of study - " + person.getYearOfStudy()).append(", ");
            }
            if (person.getMajor() != null) {
                builder.append("Major - " + person.getMajor()).append(", ");
            }
            if (person.getEmployer() != null) {
                builder.append("Employer - " + person.getEmployer());
            }
            int length = builder.length();
            if (length > 2 && builder.substring(length - 2, length).equals(", ")) {
                builder.setLength(length - 2);
            }
        }
        return builder.toString();
    }
}