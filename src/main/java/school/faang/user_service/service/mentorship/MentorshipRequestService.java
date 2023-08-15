package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipMapper mentorshipMapper;
    private final UserRepository userRepository;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (isRequesterEqualReceiver(mentorshipRequestDto)) {
            throw new IllegalArgumentException("The request cannot be sent to itself");
        }

        if (isRequesterOrReceiverZero(mentorshipRequestDto)) {
            throw new IllegalArgumentException("Requester or Receiver not found");
        }

        Optional<MentorshipRequest> mentorshipRequest = mentorshipRequestRepository.findLatestRequest(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId()
        );

        mentorshipRequest.ifPresent(s -> {
            if (s.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
                throw new IllegalArgumentException("The mentoring request already exists and 3 months have not passed yet");
            }
        });

        if (mentorshipRequest.isEmpty()) {
            MentorshipRequest mentorshipRequestNew = mentorshipRequestRepository.save(
                    mentorshipMapper.toEntity(mentorshipRequestDto));
            return mentorshipMapper.toDto(mentorshipRequestNew);
        }

        return mentorshipMapper.toDto(mentorshipRequest.get());
    }

    private boolean isRequesterEqualReceiver(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId());
    }

    private boolean isRequesterOrReceiverZero(MentorshipRequestDto mentorshipRequestDto) {
        return userRepository.findById(mentorshipRequestDto.getRequesterId()).isEmpty() || userRepository.findById(mentorshipRequestDto.getReceiverId()).isEmpty();
    }
}
