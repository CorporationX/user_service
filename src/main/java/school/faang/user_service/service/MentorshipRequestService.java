package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
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

import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestRepository repository;
    private final Predicates predicates = new Predicates();
    private final RequestMapper requestMapper;
    public static final String MENTOR_IS_ALREADY_ACCEPTED = "mentor request is already accepter";

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        val response = mentorshipRequestValidator.validate(mentorshipRequestDto, List.of(predicates.userExistsPredicate, predicates.sameUserPredicate, predicates.requestTimeExceededPredicate));
        if (!(response instanceof Validated)) {
            System.out.println(((NotValidated) response).getMessage());
        } else {
            repository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        }

    }

    public void getRequests(RequestFilterDto filter) {

        val requestFilter = requestMapper.toEntity(filter);

        Optional<List<MentorshipRequest>> mentorshipRequestList = repository.getRequests();
        if (mentorshipRequestList.isPresent()) {
            val result = mentorshipRequestList.get().stream()
                    .filter(predicates.isDescriptionEmptyPredicate)
                    .filter(request -> {
                        return predicates.areAuthorsMatch.test(request, requestFilter);
                    })
                    .filter(request -> {
                        return predicates.isRecieverMatch.test(request, requestFilter);
                    })
                    .filter(request -> {
                        return predicates.isStatusMatch.test(request, requestFilter);
                    })
                    .toList();
            if (!result.isEmpty()) {
                System.out.println("here are the filtered result = " + result);
            } else {
                System.out.println("results are empty ");
            }
        } else {
            System.out.println("database is empty");
        }
    }

    void acceptRequest(long id) throws Exception {
        MentorshipRequest request = repository.getMentorshipRequestById(id);
        if (request.getStatus() != ACCEPTED) {
            repository.updateMentorshipRequestStatusByRequesterId(id, ACCEPTED);
        } else if (request.getStatus() == ACCEPTED) {
            throw new Exception(MENTOR_IS_ALREADY_ACCEPTED);
        }
    }

    void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = repository.getMentorshipRequestById(id);
        repository.updateMentorshipRequestStatusWithReasonByRequesterId(id, REJECTED, rejection.getReason());

    }

}
