package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserService userService;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        User register = userService.findById(mentorshipRequestDto.getRequesterId());
        User receiver = userService.findById(mentorshipRequestDto.getReceiverId());

        if (register.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("You can only request mentorship yourself.");
        }

        LocalDateTime threeMouthAgo = LocalDateTime.now().minusMonths(3);
        Optional<MentorshipRequest> recentRequest = mentorshipRequestRepository
                .findLatestRequest(register.getId(), receiver.getId());
        if (recentRequest.isPresent()) {
            if (recentRequest.get().getCreatedAt().isAfter(threeMouthAgo)) {
                throw new IllegalArgumentException("You can only request mentorship once every 3 mouths.");
            }
        }
        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
