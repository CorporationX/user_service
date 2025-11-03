package school.faang.user_service.service.recommendation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.repository.user.UserRepository;
import school.faang.user_service.service.recommendation.validator.RecommendationRequestValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserContext userContext;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public RecommendationRequest create(RecommendationRequest recommendationRequestForSaving, List<Long> skillsId,
                                        Long receiverId) {

        Optional<RecommendationRequest> latestPendingRequest = recommendationRequestRepository
                .findLatestRequestByReceiverAndRequesterAndStatus(
                        userContext.getUserId(), receiverId, RequestStatus.PENDING.ordinal()
                );

        RecommendationRequestValidator.validateCreatedOrUpdatedRecommendationRequests(
                latestPendingRequest.orElse(null), userContext.getUserId(), receiverId);

        recommendationRequestForSaving.setRequester(getCurrentRequester());
        recommendationRequestForSaving.setReceiver(userRepository.getByIdOrThrow(receiverId));
        recommendationRequestForSaving.setStatus(RequestStatus.PENDING);
        setSkillRequestBySkillIds(recommendationRequestForSaving, skillsId);

        recommendationRequestForSaving = recommendationRequestRepository.save(recommendationRequestForSaving);

        log.info("RecommendationRequest was created with id {}", recommendationRequestForSaving.getId());

        return recommendationRequestForSaving;
    }

    @Transactional(readOnly = true)
    public List<RecommendationRequest> getByFilters(RecommendationRequestFilterDto filters) {
        List<RecommendationRequest> all = recommendationRequestRepository.findAll();
        return buildFilterStream(all, filters).toList();
    }

    @Transactional(readOnly = true)
    public RecommendationRequest getById(long id) {
        return recommendationRequestRepository.getByIdOrThrow(id);
    }

    @Transactional
    public void accept(long id) {
        RecommendationRequest updated = getById(id);
        RecommendationRequestValidator.validateAcceptingAndRejecting(
                userContext.getUserId(), updated.getReceiver().getId(), updated.getStatus());
        updated.setStatus(RequestStatus.ACCEPTED);
        recommendationRequestRepository.save(updated);

        log.info("RecommendationRequest was accepted {} at {}", id, updated.getUpdatedAt());
    }

    @Transactional
    public void reject(long id, RejectionDto rejection) {
        RecommendationRequest updated = getById(id);
        RecommendationRequestValidator.validateAcceptingAndRejecting(
                userContext.getUserId(), updated.getReceiver().getId(), updated.getStatus());
        updated.setStatus(RequestStatus.REJECTED);
        updated.setRejectionReason(rejection.getReason());
        recommendationRequestRepository.save(updated);

        log.info("RecommendationRequest was rejected {} at {}", id, updated.getUpdatedAt());
    }

    private Stream<RecommendationRequest> buildFilterStream(List<RecommendationRequest> allRequests,
                                                            RecommendationRequestFilterDto filters) {

        Predicate<RecommendationRequest> combinedPredicate = (recommendationRequest) -> true;

        if (Objects.nonNull(filters.getRequesterId())) {
            combinedPredicate =
                    combinedPredicate.and(
                            recommendationRequest -> recommendationRequest.getRequester().getId()
                                    .equals(filters.getRequesterId()));
        }
        if (Objects.nonNull(filters.getReceiverId())) {
            combinedPredicate = combinedPredicate.and(
                    recommendationRequest -> recommendationRequest.getReceiver().getId()
                            .equals(filters.getReceiverId())
            );
        }
        if (Objects.nonNull(filters.getStatus())) {
            combinedPredicate = combinedPredicate.and(
                    recommendationRequest -> recommendationRequest.getStatus()
                            .equals(filters.getStatus())
            );
        }
        if (Objects.nonNull(filters.getMessageContains())) {
            combinedPredicate = combinedPredicate.and(
                    recommendationRequest -> recommendationRequest.getMessage().toLowerCase()
                            .contains(filters.getMessageContains().toLowerCase())
            );
        }
        return allRequests.stream().filter(combinedPredicate);
    }

    private User getCurrentRequester() {
        return userRepository.getByIdOrThrow(userContext.getUserId());
    }

    private void setSkillRequestBySkillIds(RecommendationRequest recommendationRequestForSaving,
                                           List<Long> skillIds) {
        if (skillIds == null) {
            return;
        }
        List<Skill> skills = skillRepository.findAllById(skillIds);
        skills.forEach(recommendationRequestForSaving::createSkillRequest);
    }
}
