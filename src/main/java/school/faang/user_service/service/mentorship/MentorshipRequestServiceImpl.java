package school.faang.user_service.service.mentorship;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.mentorship.CreateMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.user.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filters.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilter;
    private final UserContext userContext;
    private final Period mentoringRequestLimitation;

    @Override
    public @Valid MentorshipRequestDto create(CreateMentorshipRequestDto requestDto) {
        businessRule(requestDto.mentorId());

        MentorshipRequest createMentorshipRequest = mentorshipRequestRepository
                .create(userContext.getUserId(),
                        requestDto.mentorId(),
                        requestDto.description());

        return mentorshipRequestMapper.toMentorshipRequestDto(createMentorshipRequest);
    }

    @Override
    public List<@Valid MentorshipRequestDto> getByFilters(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> mentorshipRequestAll = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter itemFilter : mentorshipRequestFilter) {
            if (itemFilter.isApplicable(filter)) {
                mentorshipRequestAll = itemFilter.apply(mentorshipRequestAll, filter);
            }
        }

        return mentorshipRequestAll
                .map(mentorshipRequestMapper::toMentorshipRequestDto)
                .toList();
    }

    @Override
    public void accept(long requestId) {
        Optional<MentorshipRequest> mentorshipRequest = getMentoringByIdMentee(requestId);
        mentorshipRequest.ifPresent((res) -> {
            res.setStatus(RequestStatus.ACCEPTED);
            mentorshipRequestRepository.save(res);
        });
    }

    @Override
    public void reject(long requestId, RejectionDto rejectionDto) {
        Optional<MentorshipRequest> mentorshipRequest = getMentoringByIdMentee(requestId);
        mentorshipRequest.ifPresent((res) -> {
            res.setStatus(RequestStatus.REJECTED);
            mentorshipRequestRepository.save(res);
        });
    }

    private Optional<MentorshipRequest> getMentoringByIdMentee(Long requestId) {
        MentorshipRequestFilterDto filter = new MentorshipRequestFilterDto(
                requestId,
                null,
                RequestStatus.PENDING);

        List<@Valid MentorshipRequestDto> mentorshipRequests = getByFilters(filter);

        if (mentorshipRequests.isEmpty()) {
            generateAnError("There is no mentoring request for the mentee ID that " + requestId);
        }

        Long mentorshipRequestId = mentorshipRequests.get(0).id();

        return mentorshipRequestRepository.findById(mentorshipRequestId);

    }

    private void generateAnError(String message) {
        log.error(message);
        throw new DataValidationException(message);
    }

    private void businessRule(Long mentorId) {
        if (userContext.getUserId() == mentorId) {
            generateAnError("The user cannot send a request to himself");
        }

        Optional<MentorshipRequest> mentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(userContext.getUserId(), mentorId);

        if (mentorshipRequest.isPresent()
                && mentorshipRequest.get().getStatus() != RequestStatus.REJECTED) {

            MentorshipRequest mentorshipReq = mentorshipRequest.get();

            if (mentorshipReq.getStatus() == RequestStatus.ACCEPTED) {
                generateAnError("A mentor cannot accept a request from a user if he is already their mentor.");
            }

            LocalDateTime createdAt = mentorshipReq.getCreatedAt();
            LocalDateTime nextRequestDate = createdAt.plus(mentoringRequestLimitation);

            if (LocalDateTime.now().isBefore(nextRequestDate)) {
                long months = mentoringRequestLimitation.toTotalMonths();
                generateAnError("A request for mentoring can only be made once per " + months + " months");
            } else {
                generateAnError("The mentoring request already exists and is in the status " + RequestStatus.PENDING);
            }
        }
    }
}