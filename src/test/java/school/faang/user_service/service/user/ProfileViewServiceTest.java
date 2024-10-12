package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.config.redis.user.RedisProfileViewEventPublisher;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.view.ProfileViewService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static school.faang.user_service.util.users.UserTestUtil.buildUsers;

@ExtendWith(MockitoExtension.class)
class ProfileViewServiceTest {
    private static final long RECEIVER_ID = 1L;
    private static final long ACTOR_ID = 2L;
    private static final int NUMBER_OF_ACTORS = 3;

    @Mock
    private RedisProfileViewEventPublisher redisProfileViewEventPublisher;

    @InjectMocks
    private ProfileViewService profileViewService;

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Publish message by one actor id successful")
    void testAddToPublishOneActorId() {
        profileViewService.addToPublish(RECEIVER_ID, ACTOR_ID);
        List<ProfileViewEventDto> profileViewEventDtos = (List<ProfileViewEventDto>)
                ReflectionTestUtils.getField(profileViewService, "profileViewEventDtos");

        assertThat(profileViewEventDtos).isNotNull();
        ProfileViewEventDto dto = profileViewEventDtos.get(0);
        assertThat(dto.getReceiverId()).isEqualTo(RECEIVER_ID);
        assertThat(dto.getActorId()).isEqualTo(ACTOR_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Publish messages by list of actors successful")
    void testAddToPublishListOfActors() {
        List<User> actors = buildUsers(NUMBER_OF_ACTORS);

        profileViewService.addToPublish(RECEIVER_ID, actors);
        List<ProfileViewEventDto> profileViewEventDtos = (List<ProfileViewEventDto>)
                ReflectionTestUtils.getField(profileViewService, "profileViewEventDtos");

        assertThat(profileViewEventDtos).isNotEmpty();
        assertThat(profileViewEventDtos.size()).isEqualTo(NUMBER_OF_ACTORS);
    }

    @Test
    @DisplayName("Profile view event dtos is empty check successful")
    void testProfileViewDtosIsEmptySuccessful() {
        assertThat(profileViewService.profileViewEventDtosIsEmpty()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Given an exception when publish list then catch and return copyList into main list")
    void testPublishAllProfileViewEventsException() {
        int nunPublishedDtosSize = 2;
        List<User> actors = buildUsers(NUMBER_OF_ACTORS);
        doNothing()
                .doThrow(new RedisConnectionFailureException(""))
                .when(redisProfileViewEventPublisher).publish(any(ProfileViewEventDto.class));

        profileViewService.addToPublish(RECEIVER_ID, actors);
        profileViewService.publishAllProfileViewEvents();
        List<ProfileViewEventDto> profileViewEventDtos = (List<ProfileViewEventDto>)
                ReflectionTestUtils.getField(profileViewService, "profileViewEventDtos");

        assertThat(profileViewEventDtos).isNotEmpty();
        assertThat(profileViewEventDtos.size()).isEqualTo(nunPublishedDtosSize);
        verify(redisProfileViewEventPublisher, times(nunPublishedDtosSize)).publish(any(ProfileViewEventDto.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Save all user view events successful")
    void testPublishAllProfileViewEventsSuccessful() {
        List<User> actors = buildUsers(NUMBER_OF_ACTORS);

        profileViewService.addToPublish(RECEIVER_ID, actors);
        profileViewService.publishAllProfileViewEvents();
        List<ProfileViewEventDto> profileViewEventDtos = (List<ProfileViewEventDto>)
                ReflectionTestUtils.getField(profileViewService, "profileViewEventDtos");

        assertThat(profileViewEventDtos).isEmpty();
        verify(redisProfileViewEventPublisher, times(NUMBER_OF_ACTORS)).publish(any(ProfileViewEventDto.class));
    }
}