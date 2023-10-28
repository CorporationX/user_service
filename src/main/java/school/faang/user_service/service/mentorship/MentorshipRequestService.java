package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipOfferedEventDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestEvent;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipAcceptedRequestMapper;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipAcceptedEventPublisher;
import school.faang.user_service.filter.mentorship_request.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.mapper.MentorshipOfferedEventMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipAcceptedRequestMapper acceptedRequestMapper;
    private final UserService userService;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;
    private final MentorshipOfferedEventMapper mentorshipOfferedEventMapper;
    private final MentorshipRequestedEventPublisher mentorshipRequestedEventPublisher;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto dto) {
        mentorshipRequestValidator.requestValidate(dto);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(dto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest = mentorshipRequestRepository.save(mentorshipRequest);
        mentorshipRequestedEventPublisher.publish(
                new MentorshipRequestEvent(dto.getRequesterId(), dto.getReceiverId(), LocalDateTime.now()));

        dto = mentorshipRequestMapper.toDto(mentorshipRequest);
        sendNotification(dto);

        return dto;
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
    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = requestFindById(id);
        mentorshipRequestValidator.acceptRequestValidator(mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getReceiver().getId(),
                mentorshipRequest.getStatus());
    public void acceptRequest(long id) {
        MentorshipRequest request = requestFindById(id);
        mentorshipRequestValidator.acceptRequestValidator(request);

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        userService.addMentor(mentorshipRequest.getRequester().getId(),
                mentorshipRequest.getReceiver().getId());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
        List<User> newMentees = receiver.getMentees();
        newMentees.add(requester);
        receiver.setMentees(newMentees);

        List<User> newMentors = requester.getMentors();
        newMentors.add(receiver);
        requester.setMentors(newMentors);
        mentorshipAcceptedEventPublisher.publish(acceptedRequestMapper.toEventDto(request));
    }

    @Transactional
    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = requestFindById(id);
        mentorshipRequestValidator.rejectRequestValidator(mentorshipRequest.getStatus());

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    private MentorshipRequest requestFindById(long id) {
        return mentorshipRequestRepository.findById(id).orElseThrow(() -> new DataValidationException("Request is not exist"));
    }

    @Async
    private void sendNotification(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipOfferedEventDto mentorshipOfferedEventDto =
                mentorshipOfferedEventMapper.toMentorshipOfferedEvent(mentorshipRequestDto);
        mentorshipOfferedEventPublisher.publish(mentorshipOfferedEventDto);
    }
}
