package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validation.MentorshipRequestValidator;

import java.util.List;

import static school.faang.user_service.entity.RequestStatus.ACCEPTED;

@RequiredArgsConstructor
@Service
public class MentorshipRequestService {
    private final MentorshipRequestRepository repository;
    private final MentorshipRequestMapper mapper;
    private final MentorshipRequestValidator validator;

   public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);
        validator.validateRequest(mentorshipRequest);

        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        String description = mentorshipRequest.getDescription();

        MentorshipRequest newRequest = repository.create(requesterId, receiverId, description);
        return mapper.toDto(newRequest);
    }

   public void acceptRequest(long requestId) {
        validator.validateAcceptRequest(requestId);
        MentorshipRequest request = repository.findById(requestId).get();
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if(requester.getMentors() == null) {
            requester.setMentors(List.of(receiver));
        } else {
            requester.getMentors().add(receiver);
        }
        request.setStatus(ACCEPTED);
   }
}
