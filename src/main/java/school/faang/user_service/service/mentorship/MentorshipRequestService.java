package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipOfferedEventMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserService userService;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    private final MentorshipOfferedEventMapper mentorshipOfferedEventMapper;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        User requester = userService.findUserById(dto.getRequesterId());
        User receiver = userService.findUserById(dto.getReceiverId());

        //TODO: fix validator (change entity to dto)
        mentorshipRequestValidator.requestValidate(requester, receiver);
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);

        MentorshipRequestDto mentorshipRequestDto = mentorshipRequestMapper.toDto(mentorshipRequest);
        sendNotification(mentorshipRequestDto);

        return mentorshipRequestDto;
    }

    @Transactional
    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository
                .findAll()
                .stream();

        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequests, filters));

        return mentorshipRequestMapper.toDto(mentorshipRequests.toList());
    }

    @Transactional
    public void acceptRequest(long id) {
        MentorshipRequest request = requestFindById(id);
        mentorshipRequestValidator.acceptRequestValidator(request);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        request.setStatus(RequestStatus.ACCEPTED);

        List<User> newMentees = receiver.getMentees();
        newMentees.add(requester);
        receiver.setMentees(newMentees);

        List<User> newMentors = requester.getMentors();
        newMentors.add(receiver);
        requester.setMentors(newMentors);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest request = requestFindById(id);
        mentorshipRequestValidator.rejectRequestValidator(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
    }

    private MentorshipRequest requestFindById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Request is not exist"));
    }

    @Async
    private void sendNotification(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipOfferedEventDto mentorshipOfferedEventDto = mentorshipOfferedEventMapper.toMentorshipOfferedEvent(mentorshipRequestDto);
        mentorshipOfferedEventPublisher.publish(mentorshipOfferedEventDto);
    }
}
