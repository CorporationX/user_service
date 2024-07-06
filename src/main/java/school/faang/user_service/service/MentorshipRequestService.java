package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.*;
import school.faang.user_service.entity.Mentorship;
import school.faang.user_service.entity.MentorshipRequest;

import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exceptions.DuplicateMentorshipRequestException;
import school.faang.user_service.exceptions.EntityNotFoundException;
import school.faang.user_service.exceptions.ValidationException;
import school.faang.user_service.mappers.MentorshipMapper;
import school.faang.user_service.mappers.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

    public ResponseEntity<Map<String,String>> rejectRequest(RejectRequestDto rejectRequestDto){

        Optional<MentorshipRequest> foundRequest =  mentorshipRequestRepository.findById(rejectRequestDto.getId());
        if(foundRequest.isEmpty()){
            throw new EntityNotFoundException("Mentorship request not found!");
        }
        MentorshipRequest mentorshipRequest = foundRequest.get();
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejectRequestDto.getRejectReason());
        mentorshipRequestRepository.save(mentorshipRequest);
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
    public ResponseEntity<List<MentorshipRequestDto>> getAllMentorshipRequests(MentorshipRequestFilterDto filterDto) {
        Stream<MentorshipRequest> resultQuery = mentorshipRequestRepository.findAll().stream();
        if(filterDto.getDescription() != null){
            resultQuery = resultQuery.filter(req -> req.getDescription().contains(filterDto.getDescription()));
        }
        if(filterDto.getRequesterId() != null){
            resultQuery = resultQuery.filter(req->req.getRequester().getId() == filterDto.getRequesterId());
        }
        if(filterDto.getReceiverId() != null){
            resultQuery = resultQuery.filter(req->req.getReceiver().getId() == filterDto.getReceiverId());
        }
        return new ResponseEntity<>(resultQuery.map(mentorshipRequestMapper::toDto).toList(),HttpStatus.OK);
    }
//    public  ResponseEntity<MentorshipRequestDto> getAllMentorshipRequests(Integer page, MentorshipRequestFilterDto filterDto) {
//        Pageable pageable = PageRequest.of(page, 8);
//        Stream<MentorshipRequest> resultQuery = mentorshipRequestRepository.findAll().stream();
//        Specification<MentorshipRequest> specification = Specification.where(null);
//        if(!filterDto.getDescription().isEmpty()) {
//            specification = specification.and((root, query, builder) ->builder.like(root.get("description"),"%"+filterDto.getDescription()+"%"));
//        }
//        if (filterDto.getReceiverId() != null) {
//            specification = specification.and((root, query, builder) ->builder.equal(root.get("receiver"),filterDto.getReceiverId()));
//        }
//        if (filterDto.getRequesterId() != null) {
//            specification = specification.and((root, query, builder) ->builder.equal(root.get("requester"),filterDto.getRequesterId()));
//        }
//        List<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll(specification);
//        return mentorshipRequests.map(mentorshipRequestMapper::toDto);
//    }
}
