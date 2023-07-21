package school.faang.user_service.services.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.volidate.mentorship.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRepository mentorshipRepository;

    @Transactional
    public void requestMentorship(MentorshipRequestDto dto) {
        User requester = userRepository.findById(dto.getRequesterId())
                .orElseThrow(() -> new DataValidationException("Requester was not found"));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Receiver was not found"));

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequestValidator.requestValidate(requester, receiver);

        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public List<User> getRequests(long userId) {
        List<User> mentors = mentorshipRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User was nor found"))
                .getMentors();

        return mentors;
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Request is not exist"));

        mentorshipRequestValidator.acceptRequestValidator(request);

        request.setStatus(RequestStatus.ACCEPTED);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        //Какое решение лучше ?
        //первое увидел в других работах, не понял как оно сохраняет, второе написал сам

        //1
        requester.getMentors()
                .add(receiver);
        userRepository.save(requester);

        receiver.getMentees()
                .add(requester);
        userRepository.save(receiver);

        /*
        2
        List<User> newMentees = request.getReceiver().getMentees();
        newMentees.add(request.getRequester());
        request.getRequester().setMentees(newMentees);

        List<User> newMentors = request.getRequester().getMentors();
        newMentors.add(request.getReceiver());
        request.getReceiver().setMentees(newMentors);
         */

        mentorshipRequestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Request is not exist"));

        mentorshipRequestValidator.rejectRequestValidator(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
    }
}
