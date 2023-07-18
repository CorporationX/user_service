package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SubscriptionValidator {
    public void validateId(long id){
        if(id < 1){
            throw new DataValidationException("id have to me greater than 0");
        }
    }
    public void validateId(long firstId, long secondId){
        if (firstId == secondId) {
            throw new DataValidationException("User cannot follow himself");
        }
        validateId(firstId);
        validateId(secondId);
    }
}
