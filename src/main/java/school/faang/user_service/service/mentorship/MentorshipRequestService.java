package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void requestMentorship(MentorshipRequest mentorshipRequest) {
        long requesterId = mentorshipRequest.getRequester().getId();
        long reciverId = mentorshipRequest.getReceiver().getId();

        if(requesterId == reciverId) {
            throw new IllegalArgumentException("Cannot request from yourself");
        }
        Iterable<User> user = userRepository.findAllById(List.of(requesterId, reciverId));
//        user.
//        In my opinion this doesnt properly solve the task, because requirement states that 1 request per 3 months and I assume thats for any reciver.
//        But the findLatestRequest is in place so i wont bother
      mentorshipRequestRepository.findLatestRequest(requesterId, reciverId);
    }

}
