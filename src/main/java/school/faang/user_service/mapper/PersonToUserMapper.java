package school.faang.user_service.mapper;

import com.json.student.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.User;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersonToUserMapper {
    PersonToUserMapper INSTANCE = Mappers.getMapper(PersonToUserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", expression = "java(combineName(person.getFirstName(), person.getLastName()))")
    @Mapping(target = "email", source = "contactInfo.email")
    @Mapping(target = "phone", source = "contactInfo.phone")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "city", source = "contactInfo.address.city")
    @Mapping(target = "experience", constant = "0")
    @Mapping(target = "password", expression = "java(generateRandomPassword())")
    @Mapping(target = "aboutMe", expression = "java(mapAboutMe(person))")
    User personToUser(Person person);

    default String generateRandomPassword() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    default String combineName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    default String mapAboutMe(Person person) {
        StringBuilder aboutMe = new StringBuilder();
        if (person.getPreviousEducation() != null) {
            List<String> educations = person.getPreviousEducation().stream()
                    .map(ed -> ed.getDegree() + " from " + ed.getInstitution() + " (" + ed.getCompletionYear() + ")")
                    .collect(Collectors.toList());
            aboutMe.append("Previous Education: ").append(String.join(", ", educations)).append(". ");
        }

        if (person.getEmployer() != null) {
            aboutMe.append("Currently employed at: ").append(person.getEmployer()).append(". ");
        }

        return aboutMe.toString().trim();
    }
}

