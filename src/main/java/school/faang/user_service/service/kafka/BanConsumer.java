package school.faang.user_service.service.kafka;

import faang.school.event.UserBanEvent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

@RequiredArgsConstructor
@Service
public class BanConsumer {

    private final UserService userService;

    @KafkaListener(topics = "${spring.kafka.topics.user-ban-topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void banUserListener(@NotNull UserBanEvent event) {
        userService.banUser(event.getUserId());
    }
}
