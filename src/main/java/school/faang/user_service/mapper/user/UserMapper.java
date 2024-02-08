package school.faang.user_service.mapper.user;


import com.json.student.PersonSchemaV2;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "countryId", source = "country.id")
    UserDto toDto(User user);

    @Mapping(target = "country.id", source = "countryId")
    User toEntity(UserDto userDto);

    List<UserDto> toUserDtoList(List<User> users);

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
