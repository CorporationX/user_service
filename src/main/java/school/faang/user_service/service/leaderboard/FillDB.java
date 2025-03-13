package school.faang.user_service.service.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.leaderboard.UserActivityDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class FillDB {
    private final UserActivityService userActivityService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;

    public void work() {
        Random random = new Random();
        Country country = Country.builder()
                .title("country")
                .build();
        countryRepository.save(country);
        for (int i = 0; i < 10; ++i) {
            User user = User.builder()
                    .username("username " + i)
                    .country(country)
                    .email("email.com" + i)
                    .password("password" + i)
                    .active(true)
                    .build();
            userRepository.save(user);
            userActivityService.recordUserAction(new UserActivityDto((long) i, user.getId(),
                       "username " + i, country.getTitle()), random.nextInt(10, 1000));

        }
    }
}
