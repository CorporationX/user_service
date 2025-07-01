package school.faang.user_service.model.promotion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("promotion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionRedisModel implements Serializable {
    @Id
    private String key;
    private Integer countView;
    private Long id;
    @Indexed
    private Long eventId;
    @Indexed
    private Long userId;
    private Long tariffId;
}

