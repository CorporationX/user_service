//package school.faang.user_service.messaging.skill;
//
//import com.fasterxml.jackson.databind.json.JsonMapper;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.stereotype.Component;
//import school.faang.user_service.dto.skill.SkillOfferEvent;
//import school.faang.user_service.messaging.EventPublisher;
//
//@Component
//public class SkillOfferedEventPublisher extends EventPublisher<SkillOfferEvent> {
//    public SkillOfferedEventPublisher(RedisTemplate<String, Object> redisTemplate,
//                                      ChannelTopic skillOfferedTopic,
//                                      JsonMapper mapper) {
//        super(redisTemplate, skillOfferedTopic, mapper);
//    }
//}
