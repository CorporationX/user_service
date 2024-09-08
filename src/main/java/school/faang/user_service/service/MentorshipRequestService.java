package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.RequestMapper;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;
import school.faang.user_service.validator.Predicates;
import school.faang.user_service.validator.validatorResult.NotValidated;
import school.faang.user_service.validator.validatorResult.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;
    private final Predicates predicates;
    private final RequestMapper requestMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        val res = mentorshipRequestValidator.validate(mentorshipRequestDto, List.of(predicates.userExistsPredicate, predicates.sameUserPredicate, predicates.requestTimeExceededPredicate));
        if (!(res instanceof Validated)) {
            if (res instanceof NotValidated notValidated) {
                System.out.println(notValidated.getMessage());
            }
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        }

    }
}
