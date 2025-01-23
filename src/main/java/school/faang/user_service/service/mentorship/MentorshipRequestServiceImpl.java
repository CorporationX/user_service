package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.adapter.mentorship.MentorshipRequestRepositoryAdapter;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    @Value("${app.number_months_membership}")
    @Setter
    private Integer numberMonthsMembership;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestRepositoryAdapter mentorshipRequestRepositoryAdapter;
    private final UserRepository userRepository;
    private final MentorshipMapper mentorshipMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;
    private static final RequestStatus ACCEPTED = RequestStatus.ACCEPTED;
    private static final RequestStatus REJECTED = RequestStatus.REJECTED;

    @Override
    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        return mentorshipMapper.toDto(createRequestMentorship(mentorshipRequestDto));
    }

    @Override
    public MentorshipRequestDto acceptRequest(Long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);
        checkDataBeforeAcceptRequest(mentorshipRequest);
        acceptRequestMentorship(mentorshipRequest);
        return mentorshipMapper.toDto(mentorshipRequest);
    }

    @Override
    public MentorshipRequestDto rejectRequest(MentorshipRejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(rejection.getId());
        checkDataBeforeRejectRequest(mentorshipRequest);
        rejectRequestMentorship(mentorshipRequest, rejection);
        return mentorshipMapper.toDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = StreamSupport
                .stream(mentorshipRequestRepository.findAll().spliterator(), false);
        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequests, filters));
        return mentorshipRequests.map(mentorshipMapper::toDto).toList();
    }

    private void acceptRequestMentorship(MentorshipRequest mentorshipRequest) {
        User mentor = mentorshipRequest.getReceiver();
        mentorshipRequest.getRequester().getMentors().add(mentor);
        mentorshipRequest.setStatus(ACCEPTED);
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private void checkDataBeforeRejectRequest(MentorshipRequest mentorshipRequest) {
        if (Objects.equals(mentorshipRequest.getStatus(), ACCEPTED)) {
            throw new DataValidationException("Accepted request can't be rejected.");
        }
        if (Objects.equals(mentorshipRequest.getStatus(), REJECTED)) {
            throw new DataValidationException("The reject is already rejected.");
        }
    }

    private void rejectRequestMentorship(MentorshipRequest mentorshipRequest, MentorshipRejectionDto rejection) {
        mentorshipRequest.setStatus(REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);
    }

    private MentorshipRequest getMentorshipRequest(Long id) {
        return mentorshipRequestRepositoryAdapter.getMentorshipRequest(id);
    }

    private void checkDataBeforeAcceptRequest(MentorshipRequest mentorshipRequest) {
        List<User> mentors = mentorshipRequest.getRequester().getMentors();
        User mentor = mentorshipRequest.getReceiver();
        if (mentors.contains(mentor)) {
            throw new DataValidationException(String.format("The mentor %s is already helps user %s",
                    mentor.getUsername(),
                    mentorshipRequest.getRequester().getUsername()));
        }
    }

    private void checkDataBeforeCreateRequest(MentorshipRequestDto mentorshipRequestDto) {
        isUserExist(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        checkIsDifferentUsers(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        checkLastRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
    }

    private void checkIsDifferentUsers(Long requesterId, Long receiverId) {
        if (Objects.equals(requesterId, receiverId)) {
            throw new DataValidationException("The user can't send request to himself.");
        }
    }

    private void checkLastRequest(Long requesterId, Long receiverId) {
        LocalDateTime dateNow = LocalDateTime.now();
        MentorshipRequest lastRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElse(null);
        if (lastRequest == null) {
            return;
        }
        LocalDateTime dateFrom = lastRequest.getCreatedAt().plusMonths(numberMonthsMembership);
        if (dateNow.isBefore(dateFrom)) {
            throw new DataValidationException("You can send only one request for mentorship in period");
        }
    }

    private void isUserExist(Long requesterId, Long receiverId) {
        checkUserInRepository(requesterId);
        checkUserInRepository(receiverId);
    }

    private void checkUserInRepository(Long id) {
        if (!userRepository.existsById(id)) {
            throw new DataValidationException(String.format("No such user in database with id: %d", id));
        }
    }

    private MentorshipRequest createRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
    }
}
