package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.adapter.MentorshipRequestRepositoryAdapter;
import school.faang.user_service.dto.mentorship.MentorshipRequestRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestResponseDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestResponseMapper;
import school.faang.user_service.repository.adapter.UserRepositoryAdapter;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private static final int NUMBER_OF_MONTH_THAT_MUST_PASS = 3;

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestRepositoryAdapter mentorshipRequestRepositoryAdapter;
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private final MentorshipRequestResponseMapper mentorshipRequestResponseMapper;

    @Transactional
    public MentorshipRequestResponseDto requestMentorship(MentorshipRequestRequestDto mentorshipRequestRequestDto) {
        Long requesterId = mentorshipRequestRequestDto.requesterId();
        Long receiverId = mentorshipRequestRequestDto.receiverId();

        validateRequestMentorship(requesterId, receiverId);

        mentorshipRequestRepository.create(requesterId, receiverId, mentorshipRequestRequestDto.description());

        MentorshipRequest createdMentorshipRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElseThrow(() -> new DataValidationException("An error occurred while saving the mentorship request"));

        return mentorshipRequestResponseMapper.toDto(createdMentorshipRequest);
    }

    public List<MentorshipRequestResponseDto> getRequests(MentorshipRequestFilterDto filter) {
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();

        for (MentorshipRequestFilter mentorshipRequestFilter : mentorshipRequestFilters) {
            if (mentorshipRequestFilter.isApplicable(filter)) {
                mentorshipRequests = mentorshipRequestFilter.apply(mentorshipRequests, filter);
            }
        }

        return mentorshipRequestResponseMapper.toDtoList(mentorshipRequests.toList());
    }

    @Transactional
    public MentorshipRequestResponseDto acceptRequest(long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepositoryAdapter.findById(id);

        User requester = mentorshipRequest.getRequester();
        User receiver = mentorshipRequest.getReceiver();

        if (requester.getMentors().contains(receiver)) {
            throw new DataValidationException("Recipient is already a mentor to the requestor");
        }

        requester.getMentors().add(receiver);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

        return mentorshipRequestResponseMapper.toDto(mentorshipRequest);
    }

    @Transactional
    public MentorshipRequestResponseDto rejectRequest(long id, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepositoryAdapter.findById(id);

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.reason());

        return mentorshipRequestResponseMapper.toDto(mentorshipRequest);
    }

    private void validateRequestMentorship(Long requesterId, Long receiverId) {
        if (!userRepositoryAdapter.existsById(requesterId)) {
            throw new DataValidationException("User with identifier \"" + requesterId + "\" does not exist");
        }

        if (!userRepositoryAdapter.existsById(receiverId)) {
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
    }
}
