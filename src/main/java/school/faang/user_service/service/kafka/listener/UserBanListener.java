package school.faang.user_service.service.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserBanDto;
import school.faang.user_service.service.user.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanListener {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topics.user-ban}",
            groupId = "${spring.kafka.consumer.groups.user-ban}"
    )
    public void userBanListener(String message) {
        log.info("Received request from kafka to ban user with ID {}", message);
        UserBanDto userBanDto;
        try {
            userBanDto = objectMapper.readValue(message, UserBanDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while deserializing UserBanDto", e);
            throw new RuntimeException(e);
        }
        userService.banUser(userBanDto);
    }
}
