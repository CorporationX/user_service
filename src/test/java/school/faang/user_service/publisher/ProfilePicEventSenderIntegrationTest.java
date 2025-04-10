package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import school.faang.user_service.AbstractIntegrationTest;
import school.faang.user_service.config.kafka.properties.ProfilePicTopicProperties;
import school.faang.user_service.event.ProfilePicEvent;
import school.faang.user_service.exception.EventSerializationException;

@EmbeddedKafka(
        topics = "${spring.kafka.topic.profile-pic.name}",
        partitions = 1,
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
class ProfilePicEventSenderIntegrationTest extends AbstractIntegrationTest {

    private static final long USER_ID = 1L;

    private static final ProfilePicEvent PROFILE_PIC_EVENT =
            new ProfilePicEvent(USER_ID, "user_profile_photos/123-test.jpg");

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired private ConsumerFactory<String, String> consumerFactory;

    @Autowired private ProfilePicTopicProperties profilePicTopicProperties;

    @Autowired private ProfilePicEventPublisher profilePicEventSender;

    @Test
    void publishSuccessfully() {
        Consumer<String, String> consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, profilePicTopicProperties.name());

        profilePicEventSender.publish(PROFILE_PIC_EVENT);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer);

        Assertions.assertThat(records).isNotEmpty();
        Assertions.assertThat(
                        records.records(profilePicTopicProperties.name()).iterator().next().value())
                .isEqualTo(serializeProfilePicEvent());

        consumer.close();
    }

    private String serializeProfilePicEvent() {
        try {
            return objectMapper.writeValueAsString(PROFILE_PIC_EVENT);
        } catch (JsonProcessingException e) {
            throw new EventSerializationException("An error occurred while serializing the event");
        }
    }
}
