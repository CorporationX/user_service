package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    @Value("${app.number_months_membership}")
    private final int numberMonthsMembership;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipMapper mentorshipMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        checkDataBeforeCreateRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
        return mentorshipMapper.toDto(mentorshipRequest);
    }

    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filters) {
        Stream<MentorshipRequest> mentorshipRequests = StreamSupport
                .stream(mentorshipRequestRepository.findAll().spliterator(), false);
        mentorshipRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(mentorshipRequests, filters));
        return mentorshipRequests.map(mentorshipRequest -> mentorshipMapper.toDto(mentorshipRequest)).toList();
    }

    public MentorshipRequestDto acceptRequest(Long id) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(id);
        checkDataBeforeAcceptRequest(mentorshipRequest);

        User mentor = mentorshipRequest.getReceiver();
        mentorshipRequest.getRequester().getMentors().add(mentor);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());

        return mentorshipMapper.toDto(mentorshipRequest);
    }

    public MentorshipRequestDto rejectRequest(MentorshipRejectionDto rejection) {
        MentorshipRequest mentorshipRequest = getMentorshipRequest(rejection.getId());
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setUpdatedAt(LocalDateTime.now());
        mentorshipRequest.setRejectionReason(rejection.getReason());

        return mentorshipMapper.toDto(mentorshipRequest);
    }

    private MentorshipRequest getMentorshipRequest(Long id) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("There is no request with id %d.", id)));
        return mentorshipRequest;
    }

    private void checkDataBeforeAcceptRequest(MentorshipRequest mentorshipRequest) {
        List<User> mentors = mentorshipRequest.getRequester().getMentors();
        User mentor = mentorshipRequest.getReceiver();
        if (mentors.contains(mentorshipRequest.getReceiver())) {
            throw new IllegalArgumentException(String.format("The mentor %s is already helps user %s",
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
            throw new IllegalArgumentException("The user can't send request to himself.");
        }
    }

    private void checkLastRequest(Long requesterId, Long receiverId) {
        LocalDateTime dateNow = LocalDateTime.now();
        MentorshipRequest lastRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId)
                .orElse(null);
        if (lastRequest == null) {
            return;
        }
        LocalDateTime dateFrom = lastRequest.getUpdatedAt().plusMonths(numberMonthsMembership);
        if (dateNow.isBefore(dateFrom)) {
            throw new IllegalArgumentException("Only one request for mentorship in period");
        }
    }

    private void isUserExist(Long requesterId, Long receiverId) {
        checkUserInRepository(requesterId);
        checkUserInRepository(receiverId);
    }

    private void checkUserInRepository(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException(String.format("No such user in database with id: %d", id));
        }
    }
}