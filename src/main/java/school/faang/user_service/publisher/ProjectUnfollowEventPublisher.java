package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.ProjectFollowerEventDto;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectUnfollowEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Публикует событие отписки ProjectFollowerEventDto в Redis топик.
     *
     * @param event объект ProjectFollowerEventDto, содержащий projectId, followerId и creatorId
     */
    public void publishUnfollow(ProjectFollowerEventDto event) {
        try {
            String eventAsString = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend("unfollow_project_channel", event);
            log.info("Опубликовано событие отписки: ProjectId={}, FollowerId={}, CreatorId={}", event.getProjectId(), event.getFollowerId(), event.getCreatorId());
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации события отписки", e);
            throw new RuntimeException("Ошибка сериализации события отписки", e);
        }
    }
}
