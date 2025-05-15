package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.recommendation.RecommendationRequestException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestValidationException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.utils.Utils;
import school.faang.user_service.validator.Validator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    public static final String SIX_MONTHS_PERIOD_ERROR = "A recommendation request from the same user " +
            "to another can be sent no more than once every 6 months.";
    public static final String REQUEST_BY_ID_NOT_FOUND = "The recommendation request by ID={} was not found.";
    public static final String SKILLS_MISSING_FROM_DATABASE = "one or more skills are missing from the database";
    public static final String STATUS_HAS_NOT_BEEN_CHANGED = "The status of the recommendation request " +
            "has not been changed. Entity (id={}) have one of the next status {}";
    public static final Set<RequestStatus> CHECK_STATUS_FOR_REJECT = Set.of(
            RequestStatus.REJECTED, RequestStatus.ACCEPTED);

    private final UserService userService;
    private final SkillService skillService;

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestMapper mapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;
    //todo: Validator сделать возможность сортировки, задать порядок валидирования.
    private final List<Validator<RecommendationRequestDto>> requestValidators;
    private final List<Validator<RejectionDto>> rejectValidators;
    private final Utils utils;

    @Transactional
    public RecommendationResponseDto create(RecommendationRequestDto dto) {
        log.debug("validators count: {}", requestValidators.size());
        requestValidators.forEach(validator -> {
            log.debug("run validator: {}", validator.getClass().getName());
            validator.validate(dto);
        });
        validateTimePeriod(dto);
        RecommendationRequest entity = mapper.toEntity(dto);
        fillEntity(entity, dto);
        RecommendationRequest resultEntity = requestRepository.save(entity);
        RecommendationResponseDto resultDto = mapper.toDto(resultEntity);
        fillResponseDto(resultDto, entity);
        return resultDto;
    }

    @Transactional(readOnly = true)
    public List<RecommendationResponseDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> resultList = requestRepository.findAll();
        if (resultList.isEmpty()) {
            return Collections.emptyList();
        }

        Stream<RecommendationRequest> requests = resultList.stream();
        for (var filter : filters) {
            log.debug("run filter {}", filter.getClass().getName());
            if (filter.isApplicable(filterDto)) {
                requests = filter.apply(requests, filterDto);
            }
        }

        return requests
                .map(entity -> {
                    RecommendationResponseDto resultDto = mapper.toDto(entity);
                    fillResponseDto(resultDto, entity);
                    return resultDto;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public RecommendationResponseDto getRequest(Long id) {
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> recommendationRequestNotFoundException(id));

        RecommendationResponseDto resultDto = mapper.toDto(entity);
        fillResponseDto(resultDto, entity);
        return resultDto;
    }

    @Transactional
    public RecommendationResponseDto rejectRequest(Long id, RejectionDto rejection) {
        rejectValidators.forEach(validator -> validator.validate(rejection));
        RecommendationRequest entity = requestRepository.findById(id)
                .orElseThrow(() -> recommendationRequestNotFoundException(id));
        if (CHECK_STATUS_FOR_REJECT.contains(entity.getStatus())) {
            String errorMessage = utils.format(STATUS_HAS_NOT_BEEN_CHANGED, id, CHECK_STATUS_FOR_REJECT);
            log.error(errorMessage);
            throw recommendationRequestException(errorMessage);
        }
        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.reason());
        RecommendationRequest resultEntity = requestRepository.save(entity);

        RecommendationResponseDto resultDto = mapper.toDto(resultEntity);
        resultEntity.getSkills()
                .forEach(skillEntity -> resultDto.addSkill(skillEntity.getId()));
        resultDto.setRequesterId(resultEntity.getRequester().getId());
        resultDto.setReceiverId(resultEntity.getReceiver().getId());
        return resultDto;
    }

    /**
     * Метод проверяет, что запрос рекомендации от одного и того же пользователя к другому можно
     * отправлять не чаще, чем один раз в 6 месяцев.
     */
    private void validateTimePeriod(RecommendationRequestDto dto) {
        int requestCount = requestRepository.countRepeatedRequest(dto.getRequesterId(), dto.getReceiverId());
        if (requestCount > 0) {
            String errorMessage = utils.format("requesterId={}, receiverId={}: {}",
                    dto.getRequesterId(), dto.getReceiverId(), SIX_MONTHS_PERIOD_ERROR);
            log.error(errorMessage);
            throw new RecommendationRequestValidationException(SIX_MONTHS_PERIOD_ERROR);
        }
    }

    @NotNull
    private RecommendationRequestNotFoundException recommendationRequestNotFoundException(Long id) {
        String errorMessage = utils.format(REQUEST_BY_ID_NOT_FOUND, id);
        log.error(errorMessage, id);
        return new RecommendationRequestNotFoundException(errorMessage);
    }

    @NotNull
    private RecommendationRequestException recommendationRequestException(String message) {
        log.error(message);
        return new RecommendationRequestException(message);
    }

    private void fillEntity(RecommendationRequest entity, RecommendationRequestDto dto) {
        entity.setRequester(getUser(dto.getRequesterId()));
        entity.setReceiver(getUser(dto.getReceiverId()));
        List<Skill> skills = getSkills(dto);
        skills.forEach(skill -> {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(entity);
            skillRequest.setSkill(skill);
            entity.addSkillRequest(skillRequest);
        });
    }

    private void fillResponseDto(RecommendationResponseDto dto, RecommendationRequest entity) {
        setDtoSkills(dto, entity);
        dto.setRequesterId(entity.getRequester().getId());
        dto.setReceiverId(entity.getReceiver().getId());
    }

    private User getUser(Long userId) {
        return userService.getUserById(userId);
    }

    private List<Skill> getSkills(RecommendationRequestDto dto) {
        List<Skill> skills = skillService.getSkillsByIds(dto.getSkills());
        if (skills.size() != dto.getSkills().size()) {
            throw new RecommendationRequestException(SKILLS_MISSING_FROM_DATABASE);
        }
        return skills;
    }

    private void setDtoSkills(RecommendationResponseDto dto, RecommendationRequest entity) {
        entity.getSkills().forEach(skillRequest -> dto.addSkill(skillRequest.getId()));
    }

}
