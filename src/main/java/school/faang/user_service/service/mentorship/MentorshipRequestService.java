package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipMapper mentorshipMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("The request cannot be sent to itself");
        }

        if (mentorshipMapper.toEmpty(mentorshipRequestDto).getRequester().getId() == 0 || mentorshipMapper.toEmpty(mentorshipRequestDto).getReceiver().getId() == 0) {
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
            MentorshipRequest mentorshipRequestNew = mentorshipRequestRepository.create(
                    mentorshipMapper.toEmpty(mentorshipRequestDto).getRequester().getId(),
                    mentorshipMapper.toEmpty(mentorshipRequestDto).getReceiver().getId(),
                    mentorshipMapper.toEmpty(mentorshipRequestDto).getDescription());
            return mentorshipMapper.toDto(mentorshipRequestNew);
        }

        return mentorshipMapper.toDto(mentorshipRequest.get());
    }
}
