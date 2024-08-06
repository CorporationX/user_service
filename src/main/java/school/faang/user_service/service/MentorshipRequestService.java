package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserService userService;

    @Getter
    @Value("${variables.interval}")
    private long interval;

    @Transactional
    public MentorshipRequestDto acceptRequest(AcceptMentorshipRequestDto acceptMentorshipRequestDto) {
        MentorshipRequest editingRequest = getMentorshipRequest(acceptMentorshipRequestDto.getId());
        getLastMentorship(acceptMentorshipRequestDto);
        mentorshipRepository.create(acceptMentorshipRequestDto.getReceiverId(), acceptMentorshipRequestDto.getRequesterId());
        editingRequest.setStatus(RequestStatus.ACCEPTED);
        return mentorshipRequestMapper.toDto(editingRequest);
    }


    @Transactional
    public void rejectRequest(RejectRequestDto rejectRequestDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(rejectRequestDto.getId());
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectRequestDto.getRejectReason());
    }


    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.userExists(mentorshipRequestDto.getRequesterId());
        mentorshipRequestValidator.userExists(mentorshipRequestDto.getReceiverId());

        checkIsTrialExpired(mentorshipRequestDto);
        Optional<MentorshipRequest> earlierMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        earlierMentorshipRequest.ifPresent((request) -> {
            throw new DataValidationException("Request already exists");
        });

        MentorshipRequest createdRequest = mentorshipRequestRepository
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(createdRequest);
    }

    public List<MentorshipRequestDto> getAllMentorshipRequests(MentorshipRequestFilterDto filterDto) {
        Stream<MentorshipRequest> resultQuery = mentorshipRequestRepository.findAll().stream();
        if (filterDto.getDescription() != null) {
            resultQuery = resultQuery.filter(req -> req.getDescription().contains(filterDto.getDescription()));
        }
        if (filterDto.getRequesterId() != null) {
            resultQuery = resultQuery.filter(req -> req.getRequester().getId() == filterDto.getRequesterId());
        }
        if (filterDto.getReceiverId() != null) {
            resultQuery = resultQuery.filter(req -> req.getReceiver().getId() == filterDto.getReceiverId());
        }
        return mentorshipRequestMapper.toDtoList(resultQuery.toList());
    }

    private MentorshipRequest getMentorshipRequest(Long rejectRequestId) {
        return mentorshipRequestRepository
                .findById(rejectRequestId)
                .orElseThrow(() -> new EntityNotFoundException("Mentorship request not found!"));
    }

    private void checkIsTrialExpired(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestRepository
                .findFirstByRequesterIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getCreatedAt().minusMonths(interval)
                )
                .ifPresent((req) -> {
                    throw new DataValidationException(
                            String.format("User has one request for last %d months!", interval));
                });
    }

    private void getLastMentorship(AcceptMentorshipRequestDto acceptMentorshipRequestDto) {
        Optional<Mentorship> lastMentorship = mentorshipRepository
                .getLastMentorship(acceptMentorshipRequestDto.getReceiverId(),
                        acceptMentorshipRequestDto.getRequesterId());
        if (lastMentorship.isPresent()) {
            throw new DataValidationException("You have already accepted request!!");
        }
    }
}