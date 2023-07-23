package school.faang.user_service.validator;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
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
    public void validateFilterDto(UserFilterDto dto){
        if (dto.getPageSize() < 0 || dto.getPage() < 0 ){
            throw new DataValidationException("Page and pageSize cannot be negative integers");
        }
    }
}
