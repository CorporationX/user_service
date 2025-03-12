package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import school.faang.user_service.dto.leaderboard.UserDto;
import school.faang.user_service.entity.leaderboard.UserActivity;
import school.faang.user_service.repository.leaderboard.UserActivityRepository;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class FillDB {
    private final UserActivityService userActivityService;

    public void work() {
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            userActivityService.recordUserAction(new UserDto((long) i, (long) i,
                    "username " + i, "country"), random.nextInt(10, 1000));

        }
    }
}
