package school.faang.user_service.service.recommendation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.RecommendationRejectDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.recommandation.RecommendationRequestMapper;
import school.faang.user_service.mapper.recommandation.RecommendationRequestRejectionMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skil.SkillRequestService;
import school.faang.user_service.service.skil.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Validated
public class RecommendationRequestService {
    private static final int REQUESTS_PERIOD_DAYS = 180;
    private static final RequestStatus PENDING = RequestStatus.PENDING;
    private static final RequestStatus REJECTED = RequestStatus.REJECTED;

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestMapper requestMapper;
    private final RecommendationRequestRejectionMapper requestRejectionMapper;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;
    private final UserService userService;
    private final SkillRequestService skillRequestService;
    private final SkillService skillService;

    @Transactional
    public RecommendationRequestDto create(@NotNull @Valid RecommendationRequestDto requestDto) {
        validateRequest(requestDto);
        RecommendationRequest requestEntity = requestMapper.toEntity(requestDto);
        requestEntity.setRequester(userService.getUserById(requestDto.getRequesterId()));
        requestEntity.setReceiver(userService.getUserById(requestDto.getReceiverId()));
        requestEntity.setStatus(PENDING);
        RecommendationRequest requestSaved = requestRepository.save(requestEntity);
        requestSaved.setSkills(new ArrayList<>());
        List<Skill> skills = skillService.findAllByIDs(requestDto.getSkillsIds());
        skills.forEach(skill -> {
            SkillRequest skillRequest = skillRequestService.create(requestSaved, skill);
            requestSaved.getSkills().add(skillRequest);
        });
        requestRepository.save(requestSaved);
        return requestMapper.toDto(requestSaved);
    }

    private void validateRequest(RecommendationRequestDto requestDto)
    {
        validateUserExist(requestDto.getRequesterId());
        validateUserExist(requestDto.getReceiverId());
        validateRequestPeriod(requestDto.getRequesterId(), requestDto.getReceiverId());
        validateSkillsExist(requestDto.getSkillsIds());
    }

    public List<RecommendationRequestDto> getRequests(@NotNull RequestFilterDto filterDto) {
        Stream<RecommendationRequest> requestStream = requestRepository.findAll().stream();
        return filters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .flatMap(filter -> filter.apply(requestStream, filterDto))
                .distinct()
                .map(requestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(@NotNull @Min(1) Long id) {
        RecommendationRequest requestDB = findRequestByID(id);
        return requestMapper.toDto(requestDB);
    }

    @Transactional
    public RejectionDto rejectRequest(@NotNull @Min(1) Long id, @NotNull @Valid RecommendationRejectDto rejectionDto) {
        RecommendationRequest requestDB = findRequestByID(id);
        validateRequestOnStatusPending(requestDB);

        requestDB.setRejectionReason(rejectionDto.reason());
        requestDB.setStatus(REJECTED);
        RecommendationRequest requestSaved = requestRepository.save(requestDB);
        return requestRejectionMapper.toDto(requestSaved);
    }

    private void validateRequestOnStatusPending(@NotNull @Valid RecommendationRequest request) {
        if (!request.getStatus().equals(PENDING)) {
            throw new DataValidationException("Request cannot be rejected: id=" + request.getId());
        }
    }

    private void validateUserExist(@NotNull @Min(1) Long userId) {
        if (!userService.isUserExistByID(userId)) {
            throw new DataValidationException("User not found in database: id=" + userId);
        }
    }

    private void validateRequestPeriod(@NotNull @Min(1) Long requesterId, @NotNull @Min(1) Long receiverId) {
        Optional<RecommendationRequest> request = requestRepository.findLatestPendingRequest(requesterId, receiverId);
        if (request.isPresent()) {
            long durationDays = Duration.between(request.get().getCreatedAt(), LocalDateTime.now()).toDays();
            if (durationDays <= REQUESTS_PERIOD_DAYS) {
                throw new DataValidationException("Request period is too short: " + durationDays + " days");
            }
        }
    }

    private void validateSkillsExist(@NotNull List<Long> skillIds) {
        List<Long> existingSkillIds = skillService.findExistingSkills(skillIds);
        List<Long> missingSkills = skillIds.stream()
                .filter(skillId -> !existingSkillIds.contains(skillId))
                .toList();
        if (!missingSkills.isEmpty()) {
            throw new DataValidationException("Skills not found in database: ids" + missingSkills);
        }
    }

    private RecommendationRequest findRequestByID(@NotNull @Min(1) Long id) {
        Optional<RecommendationRequest> requestOptional = requestRepository.findById(id);
        if (requestOptional.isPresent()) {
            return requestOptional.get();
        } else {
            throw new RuntimeException("Request not found: id=" + id);
        }
    }
}
