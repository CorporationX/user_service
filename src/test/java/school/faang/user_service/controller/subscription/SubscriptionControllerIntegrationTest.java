package school.faang.user_service.controller.subscription;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import school.faang.user_service.AbstractIntegrationTest;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

class SubscriptionControllerIntegrationTest extends AbstractIntegrationTest {

    private static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();

    @Autowired private UserRepository userRepository;

    @Autowired private CountryRepository countryRepository;

    @Autowired private SubscriptionRepository subscriptionRepository;

    private long followeeId;

    private List<Long> followersIdsList;

    @BeforeEach
    public void init() {
        subscriptionRepository.deleteAll();

        Country country = Country.builder().title("Test country").build();

        Country savedCountry = countryRepository.save(country);

        User followee =
                User.builder()
                        .username("Test1")
                        .email("test1@example.com")
                        .phone("1234567890")
                        .password("password1")
                        .active(true)
                        .aboutMe("About Test 1")
                        .country(savedCountry)
                        .city("New York")
                        .experience(2)
                        .createdAt(LOCAL_DATE_TIME_NOW)
                        .updatedAt(LOCAL_DATE_TIME_NOW)
                        .build();

        User follower1 =
                User.builder()
                        .username("Test2")
                        .email("test22@example.com")
                        .phone("0987654321")
                        .password("password2")
                        .active(true)
                        .aboutMe("About Test 2")
                        .country(savedCountry)
                        .city("New York")
                        .experience(2)
                        .createdAt(LOCAL_DATE_TIME_NOW)
                        .updatedAt(LOCAL_DATE_TIME_NOW)
                        .build();

        User follower2 =
                User.builder()
                        .username("Test3")
                        .email("test2@example.com")
                        .phone("5432109876")
                        .password("password3")
                        .active(true)
                        .aboutMe("About Test 3")
                        .country(savedCountry)
                        .city("New York")
                        .experience(2)
                        .createdAt(LOCAL_DATE_TIME_NOW)
                        .updatedAt(LOCAL_DATE_TIME_NOW)
                        .build();

        followeeId = userRepository.save(followee).getId();

        long follower1Id = userRepository.save(follower1).getId();
        long follower2Id = userRepository.save(follower2).getId();

        followersIdsList = List.of(follower1Id, follower2Id);
    }

    @Test
    void getFollowersIds_shouldBeCompletedSuccessfully() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/subscriptions/follow")
                                .param("followerId", String.valueOf(followersIdsList.get(0)))
                                .param("followeeId", String.valueOf(followeeId)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/subscriptions/follow")
                                .param("followerId", String.valueOf(followersIdsList.get(1)))
                                .param("followeeId", String.valueOf(followeeId)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        MvcResult mvcResult =
                mockMvc.perform(
                                MockMvcRequestBuilders.get(
                                        "/api/v1/subscriptions/followee/{followeeId}/followers/ids",
                                        followeeId))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();

        String resultFollowersIdsList = mvcResult.getResponse().getContentAsString();
        String usersIdsListString = String.valueOf(followersIdsList).replaceAll("\\s+", "");

        Assertions.assertEquals(usersIdsListString, resultFollowersIdsList);
    }
}
