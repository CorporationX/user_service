package school.faang.user_service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorshipRequest.MentorshipAcceptedDto;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentorshipAcceptedEventPublisher {

    private final KafkaTemplate<String, MentorshipAcceptedDto> kafkaTemplate;

    public void publish(MentorshipAcceptedDto mentorshipAcceptedDto) {
        CompletableFuture<SendResult<String, MentorshipAcceptedDto>> future = kafkaTemplate.send("mentorship-accepted", mentorshipAcceptedDto);

        future.whenComplete((result, e) -> {
            if (e == null) {
                log.info("Accepted mentorship was sent: {}", result);
            } else {
                log.error("Failed to send accepted mentorship: {}", e.getMessage());
            }
        });
    }
}
