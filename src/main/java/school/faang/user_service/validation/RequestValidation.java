package school.faang.user_service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestValidation {

    private static final int SIX_MONTH_RECOMMENDATION_LIMIT = 6;

    private final UserRepository userRepository;

    private final SkillRepository skillRepository;

    private final RecommendationRequestRepository requestRepository;


    public List<Skill> validateRequest(RecommendationRequestDto dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Recommendation request cannot be null.");
        }

        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            log.warn("Ошибка проверки: сообщение пустое или null. DTO: {}", dto);
            throw new IllegalArgumentException("Message cannot be empty");
        }

        if (!userRepository.existsById(dto.getRequesterId()) || !userRepository.existsById(dto.getReceiverId())) {
            log.error("Ошибка проверки: Один или два юзера не существуют. RequesterId: {}, ReceiverId: {}",
                    dto.getRequesterId(), dto.getReceiverId());
            throw new IllegalArgumentException("Users must exist");
        }

        if (requestRepository.findLatestPendingRequest(dto.getRequesterId(), dto.getReceiverId())
                .filter(request -> request.getCreatedAt().isAfter(LocalDateTime.now().minusMonths(SIX_MONTH_RECOMMENDATION_LIMIT)))
                .isPresent()) {
            log.warn("Запрос уже существует в течение последних 6 месяцев DTO: {}", dto);
            throw new IllegalArgumentException("Request already exists within the past 6 months");
        }

        List<Long> skillIds = dto.getSkillsIds();
        if (skillIds == null) {
            log.warn("Ошибка проверки: В запросе не указаны навыки. DTO: {}", dto);
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        if (skills.size() != skillIds.size()) {
            log.warn("Некоторые навыки не существуют. Предоставленные ID навыков: {}", skillIds);
            throw new IllegalArgumentException("One or more skills do not exist");
        }
        return skills;
    }
}
