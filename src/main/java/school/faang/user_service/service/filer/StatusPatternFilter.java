package school.faang.user_service.service.filer;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;
@Component
public class StatusPatternFilter implements RequestFilter {
    @Override
    public boolean isApplication(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getStatusPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RequestFilterDto requestFilterDto) {
        return requestStream.filter(request -> request.getStatus().equals(requestFilterDto.getStatusPattern()));
//        requestStream.removeIf(request -> !request.getStatus().equals(requestFilterDto.getStatusPattern()));
//        List<RecommendationRequest> requestList = requestStream.stream()
//                .filter(recommendationRequest -> recommendationRequest.getStatus() == requestFilterDto.getStatusPattern())
//                .toList();
//        requestStream.clear();
//        requestStream.addAll(requestList);
    }
}
