package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestMapper requestMapper;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        long requesterId = requestDto.getRequester().getId();
        long receiverId = requestDto.getReceiver().getId();

        dataValidate(requesterId, receiverId, requestDto);

        MentorshipRequest newRequest = mentorshipRequestRepository.create(requesterId, receiverId, requestDto.getDescription());
        return requestMapper.toDto(newRequest);
    }

    private void dataValidate(long requesterId, long receiverId, MentorshipRequestDto requestDto) {
        userValidate(requesterId, receiverId);

        if ( mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).isPresent()) {
            MentorshipRequest latestRequest = mentorshipRequestRepository.findLatestRequest(requesterId, receiverId).get();
            if (latestRequest.getUpdatedAt().plusMonths(3).isAfter(LocalDateTime.now())) {
                throw new DataValidationException("Нельзя отправить запрос на менторство данному пользователю, т.к. должно " +
                        "пройти не менее 3-ех месяцев с момента последнего запроса");
            }
        }
    }

    private void userValidate(long requesterId, long receiverId) {
        if (!mentorshipRepository.existsById(requesterId)) {
            throw new UserNotFoundException("Пользователя, отправляющего запрос не существует");
        }
        if (!mentorshipRepository.existsById(receiverId)) {
            throw new UserNotFoundException("Пользователя, у которого запрашивают менторство не существует");
        }
        if (requesterId == receiverId) {
            throw new DataValidationException("Пользователь не может отправлять себе запрос на менторство");
        }
    }
}
