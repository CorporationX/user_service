package school.faang.user_service.validator.person;

import com.json.student.Person;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@AllArgsConstructor
public class PersonValidator {
    private UserRepository userRepository;
    private static final String PHONE_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    private static final String EMAIL_REGEX = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);


    public void validatePerson(Person person) {
        isValidType(person.getContactInfo().getEmail(), "email", EMAIL_PATTERN);
        isValidType(person.getContactInfo().getPhone(), "phone", PHONE_PATTERN);
    }

    private void isValidType(String object, String nameObject, Pattern pattern) {
        Matcher matcher = pattern.matcher(object);
        if (!matcher.matches()) {
            log.error("Incorrect {}", nameObject);
            throw new DataValidationException(String.format("Incorrect %s", nameObject));
        }
    }

}
