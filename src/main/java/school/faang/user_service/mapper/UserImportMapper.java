package school.faang.user_service.mapper;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.entity.user.Country;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.validator.user.PersonValidator;

import java.util.UUID;

@UtilityClass
public class UserImportMapper {
    public static User toUser(Person person, Country country) {
        PersonValidator.validate(person);

        return User.builder()
                .username("%s_%s".formatted(person.getFirstName(), person.getLastName()))
                .email(person.getEmail())
                .phone(person.getPhone())
                .city(person.getCity())
                .password(generateTempPassword())
                .country(country)
                .aboutMe(buildAboutMe(person))
                .build();
    }

    private static String buildAboutMe(Person person) {
        StringBuilder about = new StringBuilder();

        about.append("Faculty: ").append(person.getFaculty());
        about.append(" (Year ").append(person.getYearOfStudy()).append(")");
        about.append(", Major: ").append(person.getMajor());

        if (person.getGpa() != null) {
            about.append(", GPA: ").append(person.getGpa());
        }
        about.append(". ");


        if (person.getDegree() != null || person.getInstitution() != null) {
            about.append("Previous education: ");
            if (person.getDegree() != null) {
                about.append(person.getDegree());
            }
            if (person.getInstitution() != null) {
                about.append(" at ").append(person.getInstitution());
            }
            if (person.getCompletionYear() != null) {
                about.append(" (").append(person.getCompletionYear()).append(")");
            }
            about.append(". ");
        }

        if (person.getEmployer() != null) {
            about.append("Employer: ").append(person.getEmployer()).append(". ");
        }

        if (person.getScholarship() != null) {
            about.append("Scholarship: ").append(person.getScholarship() ? "Yes" : "No").append(". ");
        }

        return about.toString().trim();
    }

    private static String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
