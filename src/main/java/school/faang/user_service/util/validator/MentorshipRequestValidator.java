package school.faang.user_service.util.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.TimingException;
import school.faang.user_service.exception.SameEntityException;
import school.faang.user_service.exception.notFoundExceptions.MentorshipRequestNotFoundException;
import school.faang.user_service.exception.notFoundExceptions.contact.UserNotFoundException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

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
            throw new SameEntityException("Same mentor and mentee");
        }
        if (lastRequest.isPresent() && (lastRequest.get().getCreatedAt().isAfter(LocalDateTime.now().minusMonths(3)))) {
            throw new TimingException("The request can be sent once every three months");
        }
    }
}