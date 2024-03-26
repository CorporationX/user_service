package school.faang.user_service.service.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.repository.premium.PremiumRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {
    @Mock
    PremiumRepository premiumRepository;

    @InjectMocks
    SchedulerService schedulerService;

    Premium firstExpiredPremium;
    Premium secondExpiredPremium;
    Premium thirdExpiredPremium;
    Premium firstValidPremium;
    User firstUser;
    User secondUser;
    User thirdUser;
    User forthUser;
    @BeforeEach
    void setUp(){
        firstUser = User.builder()
                .id(1)
                .premium(new Premium())
                .build();
        secondUser = User.builder()
                .id(2)
                .premium(new Premium())
                .build();
        thirdUser = User.builder()
                .id(3)
                .premium(new Premium())
                .build();
        forthUser = User.builder()
                .id(4)
                .premium(new Premium())
                .build();

        firstExpiredPremium = Premium.builder()
                .id(1)
                .user(firstUser)
                .startDate(LocalDateTime.of(2023, 1,1,1,1))
                .endDate(LocalDateTime.of(2024, 3,23,1,1))
                .build();
        secondExpiredPremium = Premium.builder()
                .id(1)
                .user(secondUser)
                .startDate(LocalDateTime.of(2023, 1,1,1,1))
                .endDate(LocalDateTime.of(2024, 3,24,1,1))
                .build();
        thirdExpiredPremium = Premium.builder()
                .id(1)
                .user(thirdUser)
                .startDate(LocalDateTime.of(2023, 1,1,1,1))
                .endDate(LocalDateTime.of(2024, 3,25,1,1))
                .build();
        firstValidPremium = Premium.builder()
                .id(1)
                .user(forthUser)
                .startDate(LocalDateTime.of(2024, 1,1,1,1))
                .endDate(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
                .build();

        firstUser.setPremium(firstExpiredPremium);
        secondUser.setPremium(secondExpiredPremium);
        thirdUser.setPremium(thirdExpiredPremium);
        forthUser.setPremium(firstValidPremium);
    }

    @Test
    public void testDeleteExpiredPremium(){
        schedulerService.deleteExpiredPremium();
        Mockito.verify(premiumRepository, Mockito.times(1)).deleteAllByEndDateBefore(Mockito.any(LocalDateTime.class    ));
    }



}