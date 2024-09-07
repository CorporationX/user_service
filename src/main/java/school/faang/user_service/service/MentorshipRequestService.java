package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        val res = mentorshipRequestValidator.validate(mentorshipRequestDto);
        if (!(res instanceof Validated)) {
            if (res instanceof NotValidated notValidated) {
                System.out.println(notValidated.getMessage());
            }
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(),mentorshipRequestDto.getReceiverId(),mentorshipRequestDto.getDescription());
        }

    }
}
