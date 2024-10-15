package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.entity.User;
import school.faang.user_service.exception.DataValidationException;

import java.util.List;

@Component
public class MentorshipValidator {

    public void validateMenteesList(List<User> mentees){
        if(mentees == null){
            throw new DataValidationException("list of mentees is null");
        }
    }
}
