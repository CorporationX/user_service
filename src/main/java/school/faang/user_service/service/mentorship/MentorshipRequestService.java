package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.mentorship.MentorshipRequestException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private MentorshipRequestRepository mentorshipRequestRepository;
    private MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto acceptRequest(long id) {
        Optional<MentorshipRequest> byId = mentorshipRequestRepository.findById(id);
        if (byId.isPresent()) {
            MentorshipRequest mentorshipRequest = byId.get();
            User requester = mentorshipRequest.getRequester();
            User receiver = mentorshipRequest.getReceiver();
            List<User> requesterMentors = requester.getMentors();
            if (!requesterMentors.contains(receiver)) {
                requesterMentors.add(receiver);
                mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
            } else {
                throw new MentorshipRequestException("Already a mentor");
            }
            mentorshipRequestRepository.save(mentorshipRequest);
            return mentorshipRequestMapper.toDTO(mentorshipRequest);
        } else {
            throw new MentorshipRequestException("There is no mentorship request with this id");
        }
    }
}
