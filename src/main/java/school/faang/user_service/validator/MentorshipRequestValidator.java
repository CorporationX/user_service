package school.faang.user_service.validator;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.MentorshipRequestDto;
import school.faang.user_service.model.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestValidator {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void descriptionValidation(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getDescription() == null || mentorshipRequestDto.getDescription().isEmpty()) {
            log.info("Нет описания в запросе на менторство от пользователя с ID: {}", mentorshipRequestDto.getRequesterId());
            throw new IllegalArgumentException("Описание обязательно для запроса на менторство");
        }
    }

    public void requesterReceiverValidation(MentorshipRequestDto mentorshipRequestDto) {
        if (!userRepository.existsById(mentorshipRequestDto.getRequesterId())) {
            log.info("Пользователь, который запрашивает менторство {}, не найден", mentorshipRequestDto.getRequesterId());
            throw new DataValidationException("Пользователь, который запрашивает менторство (id" + mentorshipRequestDto.getRequesterId() + "), не найден");
        }
        if (!userRepository.existsById(mentorshipRequestDto.getReceiverId())) {
            log.info("Пользователь, у которого запрашивают менторство {}, не найден", mentorshipRequestDto.getReceiverId());
            throw new DataValidationException("Пользователь, у которого запрашивают менторство (id" + mentorshipRequestDto.getReceiverId() + "), не найден");
        }
    }

    public void lastRequestDateValidation(MentorshipRequestDto mentorshipRequestDto) {
        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId());
        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getUpdatedAt();
            if (lastRequestDate != null && lastRequestDate.plusMonths(3).isAfter(LocalDateTime.now())) {
                log.info("Пользователь id{} отправлял запрос на менторство менее 3 месяцев назад", mentorshipRequestDto.getReceiverId());
                throw new DataValidationException("Пользователь id" + mentorshipRequestDto.getRequesterId() + " отправлял запрос на менторство менее 3 месяцев назад");
            }
        } else {
            log.info("Последний запрос не найден, валидация пройдена. Пользователь, который запрашивает менторство {}. " +
                    "Пользователь, у которого запрашивают менторство {}", mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
        }
    }

    public void selfRequestValidation(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.getRequesterId().equals(mentorshipRequestDto.getReceiverId())) {
            log.info("Пользователь id{} отправил себе запрос на менторство", mentorshipRequestDto.getReceiverId());
            throw new DataValidationException("Вы не можете отправить запрос себе");
        }
    }

    public void requestValidation(Long id) {
        if (!mentorshipRequestRepository.existsById(id)) {
            log.info("Запрос id{} не найден", id);
            throw new DataValidationException("Запрос id" + id + " не найден");
        }
    }
}