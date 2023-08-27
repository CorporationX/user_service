package school.faang.user_service.messaging;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import school.faang.user_service.dto.mentorshipRequest.MentorshipAcceptedDto;

import java.util.Map;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"mentorship-accepted"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        })
class MentorshipAcceptedEventPublisherTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private MentorshipAcceptedEventPublisher mentorshipAcceptedEventPublisher;

    private final String TOPIC = "mentorship-accepted";

    @Test
    @Disabled
    void send_ShouldSendToBroker() {
        Consumer<String, MentorshipAcceptedDto> consumerServiceTest = createConsumer(TOPIC);

        mentorshipAcceptedEventPublisher.publish(mockMentorship());

        ConsumerRecord<String, MentorshipAcceptedDto> record =
                KafkaTestUtils.getSingleRecord(consumerServiceTest, TOPIC);

        MentorshipAcceptedDto received = record.value();

        Assertions.assertEquals(mockMentorship(), received);
    }

    private Consumer<String, MentorshipAcceptedDto> createConsumer(final String topicName) {
        Map<String, Object> consumerProps =
                KafkaTestUtils.consumerProps("group_consumer_test", "false", embeddedKafkaBroker);

        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Consumer<String, MentorshipAcceptedDto> consumer =
                new DefaultKafkaConsumerFactory<>(consumerProps,
                        new StringDeserializer(),
                        new JsonDeserializer<>(MentorshipAcceptedDto.class, false))
                        .createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, topicName);

        return consumer;
    }

    private MentorshipAcceptedDto mockMentorship() {
        return MentorshipAcceptedDto.builder()
                .id(1L)
                .receiverId(1L)
                .requesterId(2L)
                .build();
    }
}