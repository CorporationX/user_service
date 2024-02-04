package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.DataNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findByRequestId(id);
        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();
        requester.getMentors().add(receiver);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findRequestById(id);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }

    private MentorshipRequest findByRequestId(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("There is no mentorship request with this id"));
    }

}