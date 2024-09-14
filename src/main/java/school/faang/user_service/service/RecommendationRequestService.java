package school.faang.user_service.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.recommendation.RequestFilter;
import school.faang.user_service.service.filter.recommendation.RequestFilterDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;


@Component
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validateRecommendationRequestDto(dto);

        dto.skills().forEach(skill -> skillRequestRepository.create(skill.requestId(), skill.skillId()));

        RecommendationRequest request = mapper.toEntity(dto);
        return mapper.toDto(requestRepository.save(request));
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> recommendationRequests = requestRepository.findAll();
        List<RequestFilter> filters = new ArrayList<>();
        for (RequestFilter filter: requestFilters) {
            if (filter.isApplicable(filterDto)) {
                filters.add(filter);
            }
        }
        System.out.println(requestFilters);
        for (RequestFilter filter : filters) {
            recommendationRequests = filter.apply(filterDto, recommendationRequests);
        }
        return mapper.toDto(recommendationRequests);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = requestRepository.findById(id).orElse(null);
        return mapper.toDto(request);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> requestOpt = requestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            throw new DataValidationException("Запрашиваемого запроса нет в базе данных");
        }
        RecommendationRequest request = requestOpt.get();
        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new DataValidationException("Запрос уже был отклонён");
        }
        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new DataValidationException("Запрос уже принят, нельзя отклонить принятый запрос");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
        requestRepository.save(request);
    }

    private void validateRecommendationRequestDto(RecommendationRequestDto dto) {
        Optional<User> requester = userRepository.findById(dto.requesterId());
        if (requester.isEmpty()) {
            throw new DataValidationException("Requester отсутствует в базе данных");
        }
        Optional<User> receiver = userRepository.findById(dto.receiverId());
        if (receiver.isEmpty()) {
            throw new DataValidationException("Receiver отсутствует в базе данных");
        }
        Optional<RecommendationRequest> lastRequest = requestRepository.findLatestPendingRequest(
                dto.requesterId(),
                dto.receiverId()
        );
        if(lastRequest.isPresent() && LocalDateTime.now().isBefore(lastRequest.get().getCreatedAt().plusMonths(6))) {
            throw new DataValidationException("Запрос рекомендации можно отправлять не чаще, чем один раз в 6 месяцев");
        }
        List<SkillRequestDto> skillRequestDtos = dto.skills();
        if (skillRequestDtos == null || skillRequestDtos.isEmpty()) {
            throw new DataValidationException("Скиллы отсутствуют в запросе");
        }
        List<Long> skillIds = skillRequestDtos.stream()
                .map(SkillRequestDto::skillId)
                .toList();
        int countExistsSkill = skillRepository.countExisting(skillIds);
        if (countExistsSkill != skillIds.size()) {
            throw new DataValidationException("Не все скиллы существуют в базе данных");
        }
    }
}
