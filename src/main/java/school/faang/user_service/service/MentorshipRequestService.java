package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.dto.mentorship.filter.RequestFilterDto;

import java.util.List;
import java.util.stream.StreamSupport;

import school.faang.user_service.service.UserService;
import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final UserService userService;

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
        MentorshipRequest mentorshipRequest = findByRequestId(id);
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filters) {
        List<MentorshipRequest> mentorshipRequest = StreamSupport.stream(
                mentorshipRequestRepository.findAll().spliterator(), false).toList();
        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequest, filters));
        return mentorshipRequestMapper.toDtoList(mentorshipRequest);
    }

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        User receiver = userService.getUserById(mentorshipRequestDto.getReceiver());
        User requester = userService.getUserById(mentorshipRequestDto.getRequester());

        mentorshipRequestValidator.validateUserData(receiver, requester);

        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDTO(mentorshipRequest);
    }

    private MentorshipRequest findByRequestId(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no mentorship request with this id"));
    }

}