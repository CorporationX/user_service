package school.faang.user_service.service.recommendation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RejectionRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.ExceptionMessage;
import school.faang.user_service.exception.RejectRecommendationException;
import school.faang.user_service.service.recommendation.request.filter.Filter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.request.validator.recommendation.requestvalidator.CreateRequestDtoValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository mRecommendationReqRep;
    private final SkillRequestRepository mSkillReqRep;
    private final CreateRequestDtoValidator mCreateRequestValidator;
    private final List<Filter<RecommendationRequestFilter, RecommendationRequestDto>> mRecommendationFilter;
    private final RecommendationRequestMapper mRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {

        mCreateRequestValidator.validate(recommendationRequestDto);

        CompletableFuture<RecommendationRequestDto> createRequestFuture = CompletableFuture.supplyAsync(
                () -> mRecommendationReqRep.create(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId(),
                        recommendationRequestDto.getMessage()
                )
        ).thenApply(mRequestMapper::toDto);

        CompletableFuture<Void> createSkillsRequestFuture = CompletableFuture.runAsync(
                () -> mSkillReqRep.create(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId()
                )
        );

        CompletableFuture.allOf(createRequestFuture, createSkillsRequestFuture).join();

        return createRequestFuture.join();
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilter filter) {
        Stream<RecommendationRequestDto> requestsStream = StreamSupport
                .stream(mRecommendationReqRep.findAll().spliterator(), false)
                .map(mRequestMapper::toDto);

        for (var candidate : mRecommendationFilter) {
            if (candidate.isApplicable(filter)) {
                requestsStream = candidate.applyFilter(requestsStream, filter);
            }
        }

        return requestsStream.collect(Collectors.toList());
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        var recommendationRequest = mRecommendationReqRep.findById(id).orElseThrow();
        return mRequestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionRequestDto rejectionDto) {
        var recommendation = mRecommendationReqRep.findById(
                id
        ).orElseThrow();

        RequestStatus status = recommendation.getStatus();

        if (!status.equals(RequestStatus.PENDING)) {
            throw new RejectRecommendationException(
                    ExceptionMessage.IMPOSSIBLE_REJECTION,
                    recommendation.getStatus()
            );
        }

        recommendation.setStatus(RequestStatus.REJECTED);
        recommendation.setRejectionReason(rejectionDto.getReason());

        return mRequestMapper.toDto(recommendation);
    }
}
