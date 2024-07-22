package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private long userId;
    private String followerIdsJson;
    private List<Long> followersIds;

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        userId = 1L;
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        followersIds = List.of(1L, 2L);

        followerIdsJson = objectMapper.writeValueAsString(followersIds);
    }

    @Test
    @DisplayName("testing getUser method")
    void testGetUser() throws Exception {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUser(userId);
    }

    @Test
    @DisplayName("testing checkUserExistence method")
    void testCheckUserExistence() throws Exception {
        mockMvc.perform(get("/users/exists/{userId}", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).checkUserExistence(userId);
    }

    @Test
    @DisplayName("testing getUserFollowers method")
    void testGetUserFollowers() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers", userId))
                .andExpect(status().isOk());
        verify(userService, times(1)).getUserFollowers(userId);
    }

    @Test
    @DisplayName("testing doesFollowersExist method")
    void testDoesFollowersExist() throws Exception {
        mockMvc.perform(post("/users/exists/followers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(followerIdsJson))
                .andExpect(status().isOk());
        verify(userService, times(1)).checkAllFollowersExist(followersIds);
    }
}