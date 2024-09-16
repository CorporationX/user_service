package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.MentorshipRequestRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public void mentorshipRequestValidation(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().trim().isEmpty()) {
            throw new NoSuchElementException("Need request description");
        }
    }

    public MentorshipRequest checkingUsersInRepository(MentorshipRequestDto mentorshipRequestDto) {
        MentorshipRequest requestEntity = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        requestEntity.setRequester(userRepository.findById(mentorshipRequestDto.getRequesterId())
                .orElseThrow(() -> new NoSuchElementException("Requester %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        requestEntity.setReceiver(userRepository.findById(mentorshipRequestDto.getReceiverId())
                .orElseThrow(() -> new NoSuchElementException("Receiver %s not found".formatted(mentorshipRequestDto
                        .getRequesterId()))));

        return requestEntity;
    }

    public void checkingForIdenticalIdsUsers(MentorshipRequest requestEntity) {
        if (requestEntity.getRequester()
                .equals(requestEntity.getReceiver())) {
            throw new NoSuchElementException("Your request cannot be accepted");
        }
    }

    public void spamCheck(MentorshipRequestDto mentorshipRequestDto) {
        List<MentorshipRequest> mentorshipRequestList = mentorshipRequestRepository
                .findAllByRequesterId(mentorshipRequestDto.getRequesterId());

        MentorshipRequest lastMentorshipRequest = mentorshipRequestList.stream()
                .sorted(Comparator.comparing(MentorshipRequest::getCreatedAt)).findFirst().get();

        LocalDateTime lastRequestDate = lastMentorshipRequest.getCreatedAt();

        if (!LocalDateTime.now().isAfter(lastRequestDate.plusMonths(3))) {
            throw new RuntimeException("Request limit exceeded");
        }
    }
}
