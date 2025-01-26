package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.RequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validation.RequestValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {

    private final RecommendationRequestRepository requestRepository;

    private final RecommendationRequestMapper recommendationRequestMapper;

    private final RequestValidation requestValidation;

    private final SkillRequestRepository skillRequestRepository;

    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        log.info("Создание запроса на рекомендацию: {}", dto);

        List<Skill> skills = requestValidation.validateRequest(dto);

        RecommendationRequest request = recommendationRequestMapper.toEntity(dto);
        request = requestRepository.save(request);

        saveSkillRequests(request, skills);
        log.info("Запрос рекомендации успешно создан с использованием ID: {}", request.getId());

        return recommendationRequestMapper.toDto(request);
    }

    private void saveSkillRequests(RecommendationRequest request, List<Skill> skills) {

        for (Skill skill : skills) {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(request);
            skillRequest.setSkill(skill);
            skillRequestRepository.save(skillRequest);
        }
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        log.info("Отклонение запроса на рекомендацию ID: {}", id);
        RecommendationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("Запрос на рекомендацию с ID {} не найден", id);
            throw new IllegalStateException("Cannot reject a non pending request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request = requestRepository.save(request);
        log.info("Запрос на рекомендацию с ID: {} откланен успешно", id);
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {

        requestValidation.validateRequest(recommendationRequest);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        return create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filter) {
        log.info("Получение запросов на рекомендации с помощью фильтра: {}", filter);
        List<RecommendationRequest> requests = requestRepository.findAll();

        List<RecommendationRequestDto> result = requests.stream()
                .filter(request -> filterMatches(request, filter))
                .map(recommendationRequestMapper::toDto)
                .toList();

        log.info("Фильтр для сопоставления запросов на рекомендации: {}", filter);
        return result;
    }

    public RecommendationRequestDto getRecommendationRequests(long id) {
        RecommendationRequest request = findRequestById(id);
        return recommendationRequestMapper.toDto(request);
    }

    private RecommendationRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("запрос на рекомендацию с ID {} не найден", id);
                    return new EntityNotFoundException("Recommendation request not found");
                });
    }

    private boolean filterMatches(RecommendationRequest request, RequestFilterDto filters) {
        for (RequestFilter filter : requestFilters) {
            if (filter.isApplicable(filters) && filter.apply(Stream.of(request), filters).findAny().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
