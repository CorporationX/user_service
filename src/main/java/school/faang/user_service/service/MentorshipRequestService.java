package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.RejectRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;

    public void acceptRequest(AcceptMentorshipRequestDto acceptMentorshipRequestDto) {
        MentorshipRequest editingRequest = getMentorshipRequest(acceptMentorshipRequestDto.getId());

        Optional<Mentorship> lastMentorship = mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(), acceptMentorshipRequestDto.getRequesterId());
        lastMentorship.ifPresent((mentorship) -> {
            throw new DataValidationException("You have already accepted request!!");
        });
        mentorshipRepository.create(acceptMentorshipRequestDto.getReceiverId(), acceptMentorshipRequestDto.getRequesterId());
        editingRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(editingRequest);
    }

    public void rejectRequest(RejectRequestDto rejectRequestDto) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(rejectRequestDto.getId());
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectRequestDto.getRejectReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }


    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestValidator.existsById(mentorshipRequestDto.getRequesterId(), "Requester user does not exist!");
        mentorshipRequestValidator.existsById(mentorshipRequestDto.getReceiverId(), "Receiver user does not exist!");

        checkIsTrialExpired(mentorshipRequestDto);
        MentorshipRequest earlierMentorshipRequest = mentorshipRequestRepository
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .orElseThrow(()->new DataValidationException("Request already exists"));
        MentorshipRequest createdRequest = mentorshipRequestRepository
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(createdRequest);
    }

    public ResponseEntity<List<MentorshipRequestDto>> getAllMentorshipRequests(MentorshipRequestFilterDto filterDto) {
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
        return new ResponseEntity<>(resultQuery.map(mentorshipRequestMapper::toDto).toList(), HttpStatus.OK);
    }

    private MentorshipRequest getMentorshipRequest(Long rejectRequestId) {
        Optional<MentorshipRequest> foundRequest = mentorshipRequestRepository.findById(rejectRequestId);
        if (foundRequest.isPresent()) {
            return foundRequest.get();
        }
        throw new EntityNotFoundException("Mentorship request not found!");
    }

    private void checkIsTrialExpired(MentorshipRequestDto mentorshipRequestDto) {
        mentorshipRequestRepository.findFreshRequest(mentorshipRequestDto.getRequesterId()).ifPresent((req) -> {
            throw new DataValidationException("User has one request for last 3 months!");
        });
    }
}
