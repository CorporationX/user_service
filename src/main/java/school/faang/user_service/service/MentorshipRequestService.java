package school.faang.user_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import java.util.List;

import static school.faang.user_service.entity.RequestStatus.ACCEPTED;
import static school.faang.user_service.entity.RequestStatus.REJECTED;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final UserRepository userRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mapper;

    public MentorshipRequestDto requestMentorship(@Valid MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest mentorshipRequest = mapper.toEntity(mentorshipRequestDto);

        Long requesterId = mentorshipRequest.getRequester().getId();
        Long receiverId = mentorshipRequest.getReceiver().getId();
        String description = mentorshipRequest.getDescription();

        if (!userRepository.existsById(requesterId)) {
            throw new IndexOutOfBoundsException("Requester must be registered");
        }
        if (!userRepository.existsById(receiverId)) {
            throw new IndexOutOfBoundsException("Receiver must be registered");
        }
        if (requesterId == receiverId) {
            throw new IndexOutOfBoundsException("A requester cannot be a receiver fo itself");
        }

        Optional<MentorshipRequest> optionalLatestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId);

        if (optionalLatestRequest.isPresent()) {
            MentorshipRequest latestRequest = optionalLatestRequest.get();
            if (latestRequest.getUpdatedAt().isAfter(LocalDateTime.now().minusMonths(3))) {
                throw new RuntimeException("Request can only be made once every 3 months");
            }
        }

        MentorshipRequest newRequest = mentorshipRequestRepository.create(requesterId, receiverId, description);
        return mapper.toDto(newRequest);
    }

    public void acceptRequest(long requestId) {
        Optional<MentorshipRequest> requestOpt = mentorshipRequestRepository.findById(requestId);
        if (!requestOpt.isPresent()) {
            throw new NullPointerException("Request must exist");
        } else {
            MentorshipRequest request = requestOpt.get();
            User requester = request.getRequester();
            User receiver = request.getReceiver();
            List<User> mentorsOfRequester = requester.getMentors();

            if (mentorsOfRequester != null && mentorsOfRequester.contains(receiver)) {
                throw new IllegalArgumentException(receiver.getUsername() + "is already the requester mentor");
            }
        }

        MentorshipRequest request = mentorshipRequestRepository.findById(requestId).get();
        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if(requester.getMentors() == null) {
            requester.setMentors(List.of(receiver));
        } else {
            requester.getMentors().add(receiver);
        }
        request.setStatus(ACCEPTED);
   }

   public void rejectRequest(long requestId, RejectionDto rejection) {
       validator.validateRejectRequest(requestId, rejection);
       MentorshipRequest request = repository.findById(requestId).get();
       request.setStatus(REJECTED);
       request.setRejectionReason(rejection.getReason());
   }
}
