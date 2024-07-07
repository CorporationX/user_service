package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.*;

@Service
@AllArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;

    private static final int RECOMMENDATION_REQUEST_LIMIT = 6;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) throws EntityNotFoundException {
        var requester = findUserById(recommendationRequestDto.getRequesterId());
        var receiver = findUserById(recommendationRequestDto.getReceiverId());

        checkRecommendationFrequency(recommendationRequestDto.getCreatedAt(), recommendationRequestDto.getUpdatedAt());

        validateSkills(recommendationRequestDto.getSkills());

        var savedRecommendationRequest = saveRecommendationRequest(recommendationRequestDto);
        linkSkillsToRequest(savedRecommendationRequest, recommendationRequestDto.getSkills());

        return RecommendationRequestMapper.INSTANCE.entityToDto(savedRecommendationRequest);
    }
    private User findUserById(Long userId) throws EntityNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userId + " not found."));
    }

    private void checkRecommendationFrequency(LocalDateTime createdAt, LocalDateTime updatedAt) {
        if (isWithinSixMonthsTillNow(createdAt, updatedAt)) {
            throw new IllegalStateException("Recommendation requests can only be sent once every 6 months.");
        }
    }

    private void validateSkills(List<SkillRequestDto> skillRequestDtos) throws EntityNotFoundException {
        boolean allSkillsExist = skillRequestDtos.stream()
                .allMatch(skillReq -> doesSkillExistInDatabase(skillReq.getId()));

        if (!allSkillsExist) {
            throw new EntityNotFoundException("Some skills do not exist in the database.");
        }
    }

    private RecommendationRequest saveRecommendationRequest(RecommendationRequestDto recommendationRequestDto) {
        var recommendationRequest = RecommendationRequestMapper.INSTANCE.dtoToEntity(recommendationRequestDto);
        return recommendationRequestRepository.save(recommendationRequest);
    }

    private void linkSkillsToRequest(RecommendationRequest recommendationRequest, List<SkillRequestDto> skillRequests) {
        skillRequests.forEach(skill ->
                skillRequestRepository.create(recommendationRequest.getId(), skill.getId()));
    }

    private boolean doesSkillExistInDatabase(long skillId) {
        return skillRequestRepository.findById(skillId).isPresent();
    }

    private boolean isWithinSixMonthsTillNow(LocalDateTime createdAt, LocalDateTime updatedAt) {
        return MONTHS.between(updatedAt, now()) < RECOMMENDATION_REQUEST_LIMIT ||
                MONTHS.between(createdAt, now()) < RECOMMENDATION_REQUEST_LIMIT;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        return StreamSupport.stream(recommendationRequestRepository.findAll().spliterator(), false)
                .filter(recommendationRequest -> recommendationRequest.getRequester().getId() == filter.getRequesterId())
                .map(RecommendationRequestMapper.INSTANCE::entityToDto)
                .toList();
    }


    private boolean matchesFilter(RecommendationRequest recommendationRequest, RequestFilterDto filter){
        if (filter.getStatus() != null && !filter.getStatus().equals(recommendationRequest.getStatus())) return false;
        if (filter.getRequesterId() != null && !filter.getRequesterId().equals(recommendationRequest.getRequester().getId())) return false;
        if (filter.getReceiverId() != null && !filter.getReceiverId().equals(recommendationRequest.getReceiver().getId())) return false;
        if ((filter.getRecommendation() == null) != (recommendationRequest.getRecommendation() == null)) return false;
        if (filter.getSkills() != null && !filter.getSkills().isEmpty()) {
            Set<SkillRequestDto> requestSkillsSet = new HashSet<>(
                    SkillRequestMapper.INSTANCE.entityListToDtoList(recommendationRequest.getSkills()));
            return requestSkillsSet.containsAll(filter.getSkills());
        }
        return true;
    }

    public RecommendationRequestDto getRequest(long id) {
        return RecommendationRequestMapper.INSTANCE.entityToDto(getRecommendationRequest(id));
    }

    public RecommendationRequestDto getRequest(RequestFilterDto filter) {
        var id = filter.getId();
        return getRequest(id);
    }

    private RecommendationRequest getRecommendationRequest(long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RecommendationRequest with id: " + id + " not found."));
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        var recommendationRequest = getRecommendationRequest(id);
        checkRequestStatus(recommendationRequest);

        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.getRejectionReason());
        var savedRecommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        return RecommendationRequestMapper.INSTANCE.entityToDto(savedRecommendationRequest);
    }

    private void checkRequestStatus(RecommendationRequest recommendationRequest) {
        if (!recommendationRequest.getStatus().equals(RequestStatus.PENDING)) {
            throw new IllegalStateException("Only requests with PENDING status can be rejected.");
        }
    }
}
