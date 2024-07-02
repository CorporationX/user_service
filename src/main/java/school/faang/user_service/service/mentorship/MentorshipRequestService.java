package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        long requesterId = mentorshipRequestDto.getRequesterId();
        long receiverId = mentorshipRequestDto.getReceiverId();
        String description = mentorshipRequestDto.getDescription();
        LocalDateTime mentorshipCreationDate = mentorshipRequestDto.getCreatedAt();

        validateMentorshipRequest(requesterId, receiverId, mentorshipCreationDate);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);
        return mentorshipRequestMapper.toDto(mentorshipRequest);
    }

    private void validateMentorshipRequest(long requesterId, long receiverId, LocalDateTime mentorshipCreationDate) {
        mentorshipRequestValidator.validateMentorshipRequestReceiverAndRequesterExistence(requesterId, receiverId);
        mentorshipRequestValidator.validateReflection(requesterId, receiverId);
        mentorshipRequestValidator.validateMentorshipRequestFrequency(requesterId, receiverId, mentorshipCreationDate);
    }
}
