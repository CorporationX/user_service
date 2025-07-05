package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.kafka.events.RecommendationEvent;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = LocalDateTime.class)
public interface RecommendationEventMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "recipientId", source = "receiver.id")
    @Mapping(target = "timestamp", expression = "java(LocalDateTime.now())")
    RecommendationEvent fromRecommendation(Recommendation rec);
}
