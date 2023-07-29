package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipRequestValidator;

@RequiredArgsConstructor
@Service
public class MentorshipRequestService {
    private final MentorshipRequestRepository repository;
    private final MentorshipRequestMapper mapper;
    private final MentorshipRequestValidator validator;

   public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);
        validator.validate(mentorshipRequest);

        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        String description = mentorshipRequest.getDescription();

        MentorshipRequest newRequest = repository.create(requesterId, receiverId, description);
        return mapper.toDto(newRequest);
    }
}
