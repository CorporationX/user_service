package school.faang.user_service.service.mentorship;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestEventDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.redis.MentorshipOfferEventPublisher;

@Service
@Slf4j
@RequiredArgsConstructor
public class MentorshipRequestEventService {
    public static final String MENTORSHIP_REQUEST_LOG_MESSAGE = "User id={} created a mentoring request to mentor id={}";

    private final ObjectMapper objectMapper;
    private final MentorshipOfferEventPublisher offerEventPublisher;
    private final ChannelTopic mentorshipOfferTopic;

    public MentorshipRequestEventDto getMentorshipRequestEventDto(MentorshipRequest request) {
        return MentorshipRequestEventDto.builder().id(request.getId()).requesterId(request.getRequester().getId()).receiverId(request.getReceiver().getId()).build();
    }

    public void publishEventToChannel(MentorshipRequestEventDto mentorshipRequestEventDto) {
        try {
            String request = objectMapper.writeValueAsString(mentorshipRequestEventDto);
            offerEventPublisher.publish(mentorshipOfferTopic.getTopic(), request);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }

        log.info(MENTORSHIP_REQUEST_LOG_MESSAGE,
                mentorshipRequestEventDto.getRequesterId(),
                mentorshipRequestEventDto.getReceiverId()
        );
    }
}
