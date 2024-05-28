package school.faang.user_service.validator.csv;

import com.json.student.Person;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CsvValidator {

    private final JdbcTemplate jdbcTemplate;
    private static final String PHONE_REGEX = "^\\+?[0-9]{1,4}?[-.\\s]?\\(?[0-9]{1,4}?\\)?[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,4}[-.\\s]?[0-9]{1,9}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public void validate(Person person){
        validateIsUnique(person.getFirstName() + "_" + person.getLastName(), "username");
        validateIsUnique(person.getContactInfo().getPhone(), "phone");
        isValidType(person.getContactInfo().getPhone(), "phone", PHONE_PATTERN);
        validateIsUnique(person.getContactInfo().getEmail(), "email");
        isValidType(person.getContactInfo().getEmail(), "email", EMAIL_PATTERN);
        validateYear(person.getYearOfBirth());
    }

    private void isValidType(String object, String fieldName, Pattern pattern) {
        Matcher matcher = pattern.matcher(object);
        boolean matches = matcher.matches();
        if(!matches){
            throw new DataValidationException(String.format("Incorrect %s type: %s", fieldName, object));
        }
    }

    private void validateYear(Integer yearOfBirth) {
        if(yearOfBirth < 1900){
            throw new DataValidationException(String.format("Year cannot be earlier than 1900: %d", yearOfBirth));
        }

        if(yearOfBirth > LocalDateTime.now().getYear()){
            throw new DataValidationException(String.format("Year cannot be later than the current year: %d", yearOfBirth));
        }
    }

    private void validateIsUnique(String valueToCheck, String column) {
        String query = String.format("SELECT COUNT(*) = 0 FROM users WHERE %s = ?", column);
        boolean isUnique = Boolean.TRUE.equals(jdbcTemplate.queryForObject(query, Boolean.class, valueToCheck));
        if (!isUnique) {
            throw new DataValidationException(column + " is not unique, someone has already registered " + valueToCheck);
        }
    }
}
