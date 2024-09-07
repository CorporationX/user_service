package school.faang.user_service.requestformentoring.helper.validation;

import org.springframework.stereotype.Component;
import school.faang.user_service.requestformentoring.helper.exeptions.SelfRequestException;
import school.faang.user_service.requestformentoring.helper.exeptions.NotNullProvidedException;

@Component
public class ValidationUser {

    public void checkUserEqualsUser(Long user1, Long user2) {
        if (user1.equals(user2)) {
            throw new SelfRequestException();
        }
    }

    public <T> void checkOneTypeToNull(T value) {
        if (value == null) {
            throw new NotNullProvidedException(value);
        }
    }

}
