package school.faang.user_service.service;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.contact.Contact;
import school.faang.user_service.exceptions.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @InjectMocks
    private SubscriptionService subscriptionService;

    //positive
    @Test
    void followUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        doNothing().when(subscriptionRepository).unfollowUser(followerId, followeeId);
        subscriptionService.followUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .followUser(followerId, followeeId);
    }
    @Test
    void unfollowUserCallRepositoryMethod(){
        int followerId = 11;
        int followeeId = 15;
        doNothing().when(subscriptionRepository).unfollowUser(followerId, followeeId);
        subscriptionService.unfollowUser(followerId, followeeId);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .unfollowUser(followerId, followeeId);
    }

    @Test
    void getFollowersCallRepositoryMethod(){
        long followeeId = 15;
        UserFilterDto filterDto = createUserFilterDto();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(new User()));
        subscriptionService.getFollowers(followeeId, filterDto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFollowerId(followeeId);
    }

    @Test
    void getFollowersCountCallRepositoryMethod(){
        int userID = 15;
        int amountOfFollowers = 50;
        Mockito.when(subscriptionRepository.findFollowersAmountByFolloweeId(userID))
                        .thenReturn(amountOfFollowers);
        subscriptionRepository.findFollowersAmountByFolloweeId(userID);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFollowersAmountByFolloweeId(userID);
    }

    @Test
    void getFollowingCallRepositoryMethod(){
        long followeeId = 15;
        UserFilterDto filterDto = createUserFilterDto();
        Mockito.when(subscriptionRepository.findByFolloweeId(followeeId))
                .thenReturn(Stream.of(new User()));
        subscriptionService.getFollowers(followeeId, filterDto);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findByFollowerId(followeeId);
    }

    @Test
    void getFollowingCountCallRepositoryMethod(){
        long followeeId = 15;
        int amountOfFollowers = 50;
        Mockito.when(subscriptionRepository.findFolloweesAmountByFollowerId(followeeId))
                .thenReturn(amountOfFollowers);
        subscriptionService.getFollowingCount(amountOfFollowers);
        Mockito.verify(subscriptionRepository, Mockito.times(1))
                .findFolloweesAmountByFollowerId(followeeId);
    }

    //Exceptions
    @Test
    void followUserThrowIllegalException(){
        int followerId = -11;
        int followeeId = -15;
        Assert.assertThrows(IllegalArgumentException.class,
                ()-> subscriptionService.followUser(followerId, followeeId));
    }
    @Test
    void followUserThrowDataValidException() {
        int idUser = 11;
        Assert.assertThrows(DataValidationException.class,
                ()-> subscriptionService.followUser(idUser, idUser));
    }

    @ParameterizedTest
    @CsvSource({"-10", "0"})
    void getFollowersThrowIllegalException(long idUser){
        Assert.assertThrows(IllegalArgumentException.class,
                () -> subscriptionService.getFollowers(idUser, new UserFilterDto()));
    }



    private UserFilterDto createUserFilterDto(){
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("\\D+");
        userFilterDto.setAboutPattern("\\D+");
        userFilterDto.setEmailPattern("\\D+@\\D+");
        userFilterDto.setContactPattern("\\D+");
        userFilterDto.setCountryPattern("\\D+");
        userFilterDto.setCityPattern("\\D+");
        userFilterDto.setPhonePattern("\\d+");
        userFilterDto.setSkillPattern("\\D+");
        userFilterDto.setExperienceMin(1);
        userFilterDto.setExperienceMax(7);
        return userFilterDto;
    }
}