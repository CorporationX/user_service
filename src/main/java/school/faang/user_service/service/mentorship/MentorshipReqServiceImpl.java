package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.MentorshipReqDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.event.MentorshipRequestedEvent;
import school.faang.user_service.publisher.MentorshipRequestedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipReqServiceImpl implements MentorshipReqService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestedEventPublisher eventPublisher;

    @Override
    @Transactional
    public MentorshipReqDto requestMentorship(MentorshipReqDto dto) {
        validateRequest(dto);

        User requester = getUserById(dto.getRequesterId());
        User receiver = getUserById(dto.getReceiverId());

        if (dto.getRequesterId() == dto.getReceiverId()) {
            throw new IllegalArgumentException("Нельзя запросить менторство у себя");
        }

        Optional<MentorshipRequest> latest = mentorshipRequestRepository
                .findLatestRequest(dto.getRequesterId(), dto.getReceiverId());

        if (latest.isPresent() && latest.get().getStatus() == RequestStatus.PENDING) {
            throw new IllegalArgumentException("Запрос на менторство уже отправлен");
        }

        mentorshipRequestRepository.create(dto.getRequesterId(), dto.getReceiverId(), dto.getDescription());
        log.info("Создан запрос на менторство: {} → {}", dto.getRequesterId(), dto.getReceiverId());

        eventPublisher.publish(new MentorshipRequestedEvent(
                dto.getRequesterId(),
                dto.getReceiverId(),
                System.currentTimeMillis()
        ));

        return dto;
    }

    private void validateRequest(MentorshipReqDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Запрос на менторство не может быть null");
        }

        if (dto.getRequesterId() <= 0) {
            throw new IllegalArgumentException("ID отправителя (requesterId) должен быть больше 0, получен: " + dto.getRequesterId());
        }

        if (dto.getReceiverId() <= 0) {
            throw new IllegalArgumentException("ID получателя (receiverId) должен быть больше 0, получен: " + dto.getReceiverId());
        }

        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }

        if (dto.getDescription().length() < 5) {
            throw new IllegalArgumentException("Описание должно быть не менее 5 символов");
        }
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + userId));
    }
}
