package school.faang.user_service.service.filer;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;
@Component
public class IdPatternFilter implements RequestFilter {
    @Override
    public boolean isApplication(RequestFilterDto requestFilterDto) {
        return requestFilterDto.getIdPattern() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> requestStream, RequestFilterDto requestFilterDto) {
        return requestStream.filter(request -> request.getId() == requestFilterDto.getIdPattern());
//        requestStream.removeIf(request -> request.getId() != requestFilterDto.getIdPattern());
//        List<RecommendationRequest> requestList = requestStream.stream()
//                .filter(recommendationRequest -> recommendationRequest.getId() == requestFilterDto.getIdPattern())
//                .toList();
//        requestStream.clear();
//        requestStream.addAll(requestList);
    }
}
