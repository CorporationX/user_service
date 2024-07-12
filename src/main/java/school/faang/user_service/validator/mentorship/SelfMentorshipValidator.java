package school.faang.user_service.validator.mentorship;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.exception.ExceptionMessages;

import java.util.Objects;

@Component
public class SelfMentorshipValidator implements MentorshipValidator {

    @Override
    public void validate(MentorshipRequestDto requestDto) {
        if (Objects.equals(requestDto.getRequesterId(), requestDto.getReceiverId())) {
            throw new IllegalArgumentException(ExceptionMessages.SELF_MENTORSHIP);
        }
    }
}
