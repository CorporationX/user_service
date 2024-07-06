package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.AcceptMentorshipRequestDto;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exceptions.DuplicateMentorshipRequestException;
import school.faang.user_service.exceptions.EntityNotFoundException;
import school.faang.user_service.exceptions.ValidationException;
import school.faang.user_service.mappers.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Map;
import java.util.Optional;

@CommonsLog

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    public ResponseEntity<Map<String,String>> acceptRequest(AcceptMentorshipRequestDto acceptMentorshipRequestDto){
        Optional<MentorshipRequest> foundRequest =  mentorshipRequestRepository.findById(acceptMentorshipRequestDto.getId());
        if(foundRequest.isEmpty()){
            throw new EntityNotFoundException("Mentorship request not found!");
        }
        MentorshipRequest editingRequest = foundRequest.get();
        if (editingRequest.getRequester() == null) {
            throw new ValidationException("Requester ID cannot be null");
        }
        Optional<Mentorship> lastMentorship = mentorshipRepository.getLastMentorship(acceptMentorshipRequestDto.getReceiverId(), acceptMentorshipRequestDto.getRequesterId());
        lastMentorship.ifPresent((mentorship) -> {
            throw new ValidationException("You have already accepted request!!");
        });
        mentorshipRepository.create(acceptMentorshipRequestDto.getReceiverId(),acceptMentorshipRequestDto.getRequesterId());
        editingRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(editingRequest);
        return new ResponseEntity<>(Map.of("message","request accepted","status",HttpStatus.OK.toString()), HttpStatus.OK);
    }

    public ResponseEntity<Map<String,String>> rejectRequest(Long id,String reason){
        if(reason.isEmpty()) {
            throw new ValidationException("Reason is empty!");
        }
        Optional<MentorshipRequest> foundRequest =  mentorshipRequestRepository.findById(id);
        if(foundRequest.isEmpty()){
            throw new EntityNotFoundException("Mentorship request not found!");
        }
        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestMapper.toDto(foundRequest.get());
        mentorshipRequestDto.setStatus(RequestStatus.REJECTED);
        mentorshipRequestDto.setRejectionReason(reason);
        mentorshipRequestRepository.save(mentorshipRequestMapper.toEntity(mentorshipRequestDto));
        return new ResponseEntity<>(Map.of("message","request rejected","status",HttpStatus.OK.toString()), HttpStatus.OK);
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new ValidationException("Requester user does not exist!");
        }
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new ValidationException("Receiver user does not exist!");
        }
        mentorshipRequestRepository.findFreshRequest(mentorshipRequestDto.getRequesterId()).ifPresent((req)->{
            throw new ValidationException("User has one request for last 3 months!");
        });
        Optional<MentorshipRequest> earlierMentorshipRequest = mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        if (earlierMentorshipRequest.isPresent()) {
            throw new DuplicateMentorshipRequestException("Request already exists");
        }
        MentorshipRequest createdRequest;
        createdRequest = mentorshipRequestRepository
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());

        return mentorshipRequestMapper.toDto(createdRequest);

    }
}
