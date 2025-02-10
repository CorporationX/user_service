package school.faang.user_service.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import school.faang.user_service.service.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class KafkaConsumer {

    private final UserService userService;

    @KafkaListener(topics = "${kafka.topic.ban_user}", groupId = "ban_user_consumer")
    public void listener(Long userId) {
        System.out.println("Message recieved: " + userId);
        userService.banUser(userId);
    }
}
