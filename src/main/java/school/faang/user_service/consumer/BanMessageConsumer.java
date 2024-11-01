package school.faang.user_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.model.event.UserBanEvent;
import school.faang.user_service.service.UserService;

@Component
@Slf4j
@RequiredArgsConstructor
public class BanMessageConsumer {
    private final UserService userService;

    @KafkaListener(topics = "user_ban", groupId = "CorpX")
    public void consume(UserBanEvent userBanEvent) {
        userService.banUserById(userBanEvent.id());
    }
}
