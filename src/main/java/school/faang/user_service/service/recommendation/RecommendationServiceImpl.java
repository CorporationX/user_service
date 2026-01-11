package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.recommendation.RecommendationReceivedEvent;
import school.faang.user_service.publisher.RecommendationReceivedEventPublisher;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationReceivedEventPublisher recommendationReceivedEventPublisher;
    private final UserContext userContext;

    @Override
    public void recommendUser(long receiverId) {
        // TODO Реализовать логику рекомендации
        long authorId = userContext.getUserId();
        long id = new Random().nextInt(1000);
        recommendationReceivedEventPublisher.publish(new RecommendationReceivedEvent(
                id,
                authorId,
                receiverId
        ));
    }
}
