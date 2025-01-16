package school.faang.user_service.service.mentorship;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private static final int NUMBER_OF_MONTH_THAT_MUST_PASS = 3;

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    @Transactional
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        Long requesterId = mentorshipRequestDto.getRequesterId();
        Long receiverId = mentorshipRequestDto.getReceiverId();

        if (!userService.existsById(requesterId)) {
            throw new DataValidationException("User with identifier \"" + requesterId + "\" does not exist");
        }

        if (!userService.existsById(receiverId)) {
            throw new DataValidationException("User with identifier \"" + receiverId + "\" does not exist");
        }

        if (Objects.equals(requesterId, receiverId)) {
            throw new DataValidationException("User cannot send a mentorship request to himself");
        }

        Optional<MentorshipRequest> optionalLatestMentorshipRequest
                = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        if (optionalLatestMentorshipRequest.isPresent()) {
            MentorshipRequest latestMentorshipRequest = optionalLatestMentorshipRequest.get();

            if (LocalDateTime.now().getMonth().getValue()
                    < latestMentorshipRequest.getCreatedAt().getMonth().getValue() + NUMBER_OF_MONTH_THAT_MUST_PASS) {

                throw new DataValidationException("Mentorship request can be made once every "
                        + NUMBER_OF_MONTH_THAT_MUST_PASS + " months");
            }
        }

        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestDto.getDescription());

        MentorshipRequest createdMentorshipRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElseThrow(() -> new DataValidationException("An error occurred while saving the mentorship request"));

        return mentorshipRequestMapper.toDto(createdMentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        List<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(filter)) {
                mentorshipRequests = mentorshipRequestFilter.apply(mentorshipRequests, filter);
            }
        }

        return mentorshipRequestMapper.toDtoList(mentorshipRequests);
    }

    public MentorshipRequestDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = findById(id);

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        if (requester.getMentors().contains(receiver)) {
            throw new DataValidationException("Recipient is already a mentor to the requestor");
        }

        requester.getMentors().add(receiver);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public MentorshipRequestDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = findById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());

        mentorshipRequestRepository.save(mentorshipRequest);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    public MentorshipRequest findById(long id) {
        return mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Mentorship request with identifier \"" + id
                        + "\" does not exist"));
    }
}
