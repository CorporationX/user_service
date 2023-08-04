package school.faang.user_service.util.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.exception.RequestMentorshipException;
import school.faang.user_service.util.exception.SameMentorAndMenteeException;
import school.faang.user_service.util.exception.TimeHasNotPassedException;
import school.faang.user_service.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {

    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    public void validate(MentorshipRequestDto dto) {

        Optional<User> requester = userRepository.findById(dto.getRequesterId());
        Optional<User> receiver = userRepository.findById(dto.getReceiverId());
        Optional<MentorshipRequest> lastRequest =
                mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId());

        if (requester.isEmpty() || receiver.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (requester.get().getId() == receiver.get().getId()) {
            throw new SameMentorAndMenteeException("Same mentor and mentee");
        }
        if (lastRequest.isEmpty()) {
            throw new RequestMentorshipException("There is no requests");
        }
        if (lastRequest.get().getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
            throw new TimeHasNotPassedException("The request can be sent once every three months");
        }
    }
}