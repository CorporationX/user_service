package school.faang.user_service.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import school.faang.avro.user.UserAddSkills;
import school.faang.avro.user.UserUpdate;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserUpdateProducerImpl implements UserUpdateProducer {
    @Value("${spring.kafka.topics.user-update.name}")
    private String userUpdateTopic;

    private final KafkaTemplate<String, UserUpdate> userUpdateProducer;
    private final KafkaTemplate<String, UserAddSkills> userAddSkillsProducer;

    @Override
    public void onUserUpdate(UserUpdate dto) {
        log.info("User update event, data: {}", dto);
        userUpdateProducer.send(
                userUpdateTopic,
                String.valueOf(dto.getId()),
                dto
        );
    }

    @Override
    public void onUserAddSkills(UserAddSkills dto) {
        log.info("User add skills event, data: {}", dto);
        userAddSkillsProducer.send(
                userUpdateTopic,
                String.valueOf(dto.getId()),
                dto
        );
    }
}
