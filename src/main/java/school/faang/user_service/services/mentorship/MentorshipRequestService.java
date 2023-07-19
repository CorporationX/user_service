package school.faang.user_service.services.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.volidate.mentorship.AcceptRequestValidator;
import school.faang.user_service.volidate.mentorship.MentorshipRequestValidator;
import school.faang.user_service.volidate.mentorship.RejectRequestValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    @Autowired
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final AcceptRequestValidator acceptRequestValidator;
    private final RejectRequestValidator rejectRequestValidator;
    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public void requestMentorship(MentorshipRequestDto dto) throws Exception {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow();
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow();

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequestValidator.validate(requester, receiver);

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public List<User>  getRequests(long userId){
        List<User> mentors = mentorshipRepository.findById(userId)
                .orElseThrow().getMentors();

        return mentors;
    }

    @Transactional
    public void acceptRequest(long id) throws Exception {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(Exception::new);

        acceptRequestValidator.validate(request);

        request.setStatus(RequestStatus.ACCEPTED);

        List<User> newMentees = request.getReceiver().getMentees();
        newMentees.add(request.getRequester());
        request.getRequester().setMentees(newMentees);

        List<User> newMentors = request.getRequester().getMentors();
        newMentors.add(request.getReceiver());
        request.getReceiver().setMentees(newMentors);

        mentorshipRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) throws Exception {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(Exception::new);

        rejectRequestValidator.validator(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
    }
}
