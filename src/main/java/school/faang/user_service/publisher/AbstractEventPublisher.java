package school.faang.user_service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void convertAndSend(String topic, T event){
        try{
            String jsonObject = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, jsonObject);
            log.info("Event published to topic " + topic);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
