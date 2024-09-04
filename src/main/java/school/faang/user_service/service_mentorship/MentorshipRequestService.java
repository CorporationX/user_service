package school.faang.user_service.service_mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto_mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper_mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor

public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need request description");
        }

        // проверить что receiver и requester разные id!

        MentorshipRequest requestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        requestEntity.setRequester(userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        requestEntity.setReceiver(userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new NoSuchElementException("User %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        if (userRepository.findById(mentorshipRequestDto.getReceiverId())
                .equals(userRepository.findById(mentorshipRequestDto.getRequesterId()))) {
            throw new NoSuchElementException("Your request cannot be accepted");
        }

        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository
                .findAllByRequesterId(mentorshipRequestDto.getRequesterId());

        MentorshipRequest lastMentorshipRequest = mentorshipRequestList.stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt)).findFirst().get();

        LocalDateTime lastRequestDate = lastMentorshipRequest.getCreatedAt();

        if (!LocalDateTime.now().isAfter(lastRequestDate.plusMonths(3))) {
            throw new RuntimeException("Request limit exceeded");
        }

        return mentorshipRequestMapper.toDto(mentorshipRequestRepository.save(requestEntity));
    }
}
