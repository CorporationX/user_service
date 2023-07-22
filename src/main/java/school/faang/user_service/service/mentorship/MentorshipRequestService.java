package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.filter.MentorshipFilter;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.volidator.mentorship.MentorshipRequestValidator;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserService userService;
    private final MentorshipFilter mentorshipFilter;

    @Transactional
    public void requestMentorship(MentorshipRequestDto dto) {
        Optional<User> requester = userService.findUserById(dto.getRequesterId());
        Optional<User> receiver = userService.findUserById(dto.getReceiverId());
        mentorshipRequestValidator.requestValidate(requester, receiver);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.getAllRequests(filter.getDescription(),
                filter.getRequesterId(),
                filter.getReceiverId(),
                filter.getStatus());

        return mentorshipFilter.filterRequests(mentorshipRequests, filter);
    }

    @Transactional
    public void acceptRequest(long id) {
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);
        MentorshipRequest request = mentorshipRequestValidator.acceptRequestValidator(requestOptional);

        User requester = request.getRequester();
        User receiver = request.getReceiver();
        request.setStatus(RequestStatus.ACCEPTED);

        //Какое решение лучше ?
        //первое увидел в других работах, не понял как оно сохраняет, второе написал сам
        //1
        requester.getMentors()
                .add(receiver);
        userService.saveUser(requester);

        receiver.getMentees()
                .add(requester);
        userService.saveUser(receiver);

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
        Optional<MentorshipRequest> requestOptional = mentorshipRequestRepository.findById(id);
        MentorshipRequest request = mentorshipRequestValidator.rejectRequestValidator(requestOptional);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
    }
}
