package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;
import school.faang.user_service.validator.validatorResult.ValidationResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator implements Validator<MentorshipRequestDto>{

    public final String REQUEST_AND_RECEIVER_HAS_SAME_ID = "user can not request mentorschihp to himself";
    public final String USERS_NOT_EXIST_IN_DATABASE = "user are not exists in database";
    public final String REQUEST_TIME_EXEEDED = "The request was updated within the last 3 months.";
    public final String REQUEST_WAS_NOT_FOUND = "request was not found";

    private final MentorshipRequestRepository repository;

    @Override
    public ValidationResult validate(MentorshipRequestDto mentorshipRequestDto) {
        if (!repository.existAcceptedRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())){
            return new NotValidated(USERS_NOT_EXIST_IN_DATABASE);
        }
        if(Objects.equals(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())){
            return new NotValidated(REQUEST_AND_RECEIVER_HAS_SAME_ID);
        }

        Optional<MentorshipRequest> request = repository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        if (request.isPresent()) {
            if (request.get().getUpdatedAt().isBefore(LocalDateTime.now().minus(3, ChronoUnit.MONTHS))){
                return new Validated();
            }
            else {
                return new NotValidated(REQUEST_TIME_EXEEDED);
            }
        }
        return new NotValidated(REQUEST_WAS_NOT_FOUND);
    }
}
