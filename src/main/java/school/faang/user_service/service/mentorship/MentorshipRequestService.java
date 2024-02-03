package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRejectDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateRequestMentorship(mentorshipRequestDto);
        MentorshipRequest mentorshipRequestEntity = mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestEntity);
    }

    private void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("There are no this receiver in data base");
        } else if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("There are no this requester in data base");
        } else if (Objects.equals(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("You can not send a request to yourself");
        } else if (!isMoreThanThreeMonths(mentorshipRequestDto)) {
            throw new IllegalArgumentException("Less than 3 months have passed since last request");
        }
    }

    private boolean isMoreThanThreeMonths(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .map(mentorshipRequest -> LocalDateTime.now().isAfter(mentorshipRequest.getUpdatedAt().plusMonths(3)))
                .orElseThrow(() -> new IllegalArgumentException("There are not find request"));
    }

    public MentorshipRejectDto rejectRequest(long id, MentorshipRejectDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is blank request"));

        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(mentorshipRequest);

        return mentorshipRequestMapper.toRejectionDto(mentorshipRequest);
    }
}
