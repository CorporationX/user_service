package school.faang.user_service.service.recommendation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.RejectionRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.request.filter.Filter;
import school.faang.user_service.validator.requestvalidator.CreateRequestDtoValidator;
import school.faang.user_service.validator.requestvalidator.RejectRequestValidator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationReqRep;
    private final SkillRequestRepository skillReqRep;
    private final CreateRequestDtoValidator createRequestValidator;
    private final RejectRequestValidator rejectRequestValidator;
    private final List<Filter<RecommendationRequestFilter, RecommendationRequestDto>> recommendationFilter;
    private final RecommendationRequestMapper requestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequestDto) {

        createRequestValidator.validate(recommendationRequestDto);

        CompletableFuture<RecommendationRequestDto> createRequestFuture = CompletableFuture.supplyAsync(
                () -> recommendationReqRep.create(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId(),
                        recommendationRequestDto.getMessage()
                )
        ).thenApply(requestMapper::toDto);

        CompletableFuture<Void> createSkillsRequestFuture = CompletableFuture.runAsync(
                () -> skillReqRep.create(
                        recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId()
                )
        );

        CompletableFuture.allOf(createRequestFuture, createSkillsRequestFuture).join();

        return createRequestFuture.join();
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilter filter) {
        Stream<RecommendationRequestDto> requestsStream = StreamSupport
                .stream(recommendationReqRep.findAll().spliterator(), false)
                .map(requestMapper::toDto);

        for (var candidate : recommendationFilter) {
            if (candidate.isApplicable(filter)) {
                requestsStream = candidate.applyFilter(requestsStream, filter);
            }
        }

        return requestsStream.collect(Collectors.toList());
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        var recommendationRequest = recommendationReqRep.findById(id).orElseThrow();
        return requestMapper.toDto(recommendationRequest);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionRequestDto rejectionDto) {
        var recommendation = recommendationReqRep.findById(
                id
        ).orElseThrow();

        rejectRequestValidator.validate(recommendation);

        recommendation.setStatus(RequestStatus.REJECTED);
        recommendation.setRejectionReason(rejectionDto.getReason());

        recommendationReqRep.save(recommendation);

        return requestMapper.toDto(recommendation);
    }
}
