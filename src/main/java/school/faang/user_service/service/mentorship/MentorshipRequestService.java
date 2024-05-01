package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.event.MentorshipAcceptedEvent;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.handler.exception.DataValidationException;
import school.faang.user_service.handler.exception.EntityNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filtres.RequestFilter;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<RequestFilter> requestFilters;

    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getIdRequester();
        long receiverId = mentorshipRequestDto.getIdReceiver();
        mentorshipRequestValidator.requestMentorshipValidation(requesterId, receiverId);

        MentorshipRequest lastMentorshipRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElse(null);

        if (null == lastMentorshipRequest || LocalDateTime.now().minusMonths(3).isAfter(lastMentorshipRequest.getCreatedAt())) {
            mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());
        } else {
            throw new DataValidationException("A request for mentorship can only be made once every 3 months");
        }
    }

    public List<MentorshipRequestDto> getRequest(RequestFilterDto requestFilterDto) {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>();
        mentorshipRequestRepository.findAll().forEach(mentorshipRequests::add);

        return requestFilters.stream()
                .filter(requestFilter -> requestFilter.isApplicable(requestFilterDto))
                .flatMap(requestFilter -> requestFilter.apply(mentorshipRequests.stream(), requestFilterDto))
                .map(mentorshipRequestMapper::toDto).toList();
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such query in the database"));
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        List<User> receiverMentees = receiver.getMentees();

        if (null == receiverMentees) {
            receiverMentees = new ArrayList<>();
        }else{
            if (receiverMentees.contains(requester)) {
                throw new IllegalArgumentException("The requester is already on the mentees list for this mentor");
            }
        }
        receiverMentees.add(requester);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipAcceptedEvent mentorshipAcceptedEvent = new MentorshipAcceptedEvent(requester.getId(),receiver.getId(),id);
        mentorshipAcceptedEventPublisher.publish(mentorshipAcceptedEvent);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no request for mentoring with this ID."));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }
}