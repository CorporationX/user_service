package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateRequestMentorship(mentorshipRequestDto);

        mentorshipRequestRepository.create(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId(), mentorshipRequestDto.getDescription());

        MentorshipRequest mentorshipRequestEntity = mentorshipRequestMapper.MentorshipRequestToEntity(mentorshipRequestDto);
        mentorshipRequestEntity = mentorshipRequestRepository.save(mentorshipRequestEntity);
        return mentorshipRequestMapper.toMentorshipRequestDto(mentorshipRequestEntity);
    }

    private void validateRequestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        if (!isMoreThanThreeMonths(mentorshipRequestDto)) {
            throw new IllegalArgumentException("Less than 3 months have passed since last request");
        } else if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("There are no this receiver in data base");
        } else if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            throw new IllegalArgumentException("There are no this requester in data base");
        } else if (!mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            throw new IllegalArgumentException("You can not send a request to yourself");
        }
    }

    private boolean isMoreThanThreeMonths(MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId())
                .map(mentorshipRequest -> LocalDateTime.now().isAfter(mentorshipRequest.getUpdatedAt().plusMonths(3)))
                .orElseThrow(() -> new IllegalArgumentException("There are not find request"));
    }
}
