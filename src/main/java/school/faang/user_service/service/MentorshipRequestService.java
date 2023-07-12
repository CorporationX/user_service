package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.util.exception.UserNotFoundException;
import school.faang.user_service.util.validator.MentorshipRequestValidator;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final MentorshipRequestValidator mentorshipRequestValidator;
    private final UserRepository userRepository;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto, this);

        String description = mentorshipRequest.getDescription();
        long requesterId = mentorshipRequest.getRequester().getId();
        long receiverId = mentorshipRequest.getReceiver().getId();

        Optional<User> requester = userRepository.findById(requesterId);
        Optional<User> receiver = userRepository.findById(receiverId);
        Optional<MentorshipRequest> lastTimeRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        mentorshipRequestValidator.validate(requester, receiver, lastTimeRequest);

        mentorshipRequestRepository.create(requesterId, receiverId, description);
    }

    public User findUserById(long id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }
}
