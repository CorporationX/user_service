package school.faang.user_service.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRedisModel {
    @Id
    private String key;
    @TimeToLive
    private Long ttl;
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
}
