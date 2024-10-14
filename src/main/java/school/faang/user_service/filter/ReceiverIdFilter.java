package school.faang.user_service.filter;


import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.repository.RequestFilter;

public class ReceiverIdFilter implements RequestFilter {
    private final Long receiverId;

    public ReceiverIdFilter(Long receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public boolean apply(RecommendationRequest request) {
        return receiverId == null || request.getReceiver().getId().equals(receiverId);
    }
}