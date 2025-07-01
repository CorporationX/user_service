package school.faang.user_service.model.score;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@RedisHash("leaderboard")
@AllArgsConstructor
@EqualsAndHashCode
public class LeaderboardEntry {
    @Id
    private long userId;
    private long totalScore;
}
