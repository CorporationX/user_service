package school.faang.user_service.validator.user;

import lombok.experimental.UtilityClass;
import school.faang.user_service.dto.user.Person;
import school.faang.user_service.exception.DataValidationException;

import java.util.regex.Pattern;

@UtilityClass
public class PersonValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9\\s\\-()]{7,}$");

    public static void validate(Person person) {
        StringBuilder errors = new StringBuilder();

        validateNotBlank(person.getFirstName(), Person.Fields.firstName, errors);
        validateNotBlank(person.getLastName(), Person.Fields.lastName, errors);
        validateNotBlank(person.getCity(), Person.Fields.city, errors);
        validateNotBlank(person.getCountry(), Person.Fields.country, errors);
        validateNotBlank(person.getFaculty(), Person.Fields.faculty, errors);
        validateNotBlank(person.getMajor(), Person.Fields.major, errors);
        validateNotBlankWithRegexp(person.getEmail(), Person.Fields.email, errors, EMAIL_PATTERN);
        validateNotBlankWithRegexp(person.getPhone(), Person.Fields.phone, errors, PHONE_PATTERN);

        if (person.getYearOfStudy() == null) {
            errors.append(Person.Fields.yearOfStudy).append(" must not be empty. ");
        }

        if (!errors.isEmpty()) {
            throw new DataValidationException("Invalid Person data: %s".formatted(errors.toString().trim()));
        }
    }

    private static void validateNotBlank(String value, String fieldName, StringBuilder errors) {
        if (value == null || value.isBlank()) {
            errors.append(fieldName).append(" must not be empty. ");
        }
    }

    private static void validateNotBlankWithRegexp(String value,
                                                   String fieldName,
                                                   StringBuilder errors,
                                                   Pattern pattern) {
        if (value == null || value.isBlank()) {
            errors.append(fieldName).append(" must not be empty. ");
        } else if (!pattern.matcher(value).matches()) {
            errors.append(fieldName).append(" has invalid format. ");
        }
    }

}
