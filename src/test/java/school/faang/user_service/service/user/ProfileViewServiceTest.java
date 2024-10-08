package school.faang.user_service.service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.redis.user.RedisProfileViewEventPublisher;
import school.faang.user_service.dto.user.ProfileViewEventDto;
import school.faang.user_service.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.users.UserTestUtil.buildUsers;

@ExtendWith(MockitoExtension.class)
class ProfileViewServiceTest {
    private static final long RECEIVER_ID = 1L;
    private static final long ACTOR_ID = 2L;
    private static final int NUMBER_OF_ACTORS = 3;

    @Mock
    private UserContext userContext;

    @Mock
    private RedisProfileViewEventPublisher redisProfileViewEventPublisher;

    @InjectMocks
    private ProfileViewService profileViewService;

    @Test
    @DisplayName("Publish message by one actor id successful")
    void testPublishOneActorId() {
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        profileViewService.publish(ACTOR_ID);

        ArgumentCaptor<ProfileViewEventDto> captor = ArgumentCaptor.forClass(ProfileViewEventDto.class);
        verify(redisProfileViewEventPublisher).publish(captor.capture());
        ProfileViewEventDto captorDto = captor.getValue();

        assertThat(captorDto.getReceiverId()).isEqualTo(RECEIVER_ID);
        assertThat(captorDto.getActorId()).isEqualTo(ACTOR_ID);
    }

    @Test
    @DisplayName("Publish messages by list of actors successful")
    void testPublishListOfActors() {
        List<User> actors = buildUsers(NUMBER_OF_ACTORS);
        when(userContext.getUserId()).thenReturn(RECEIVER_ID);
        profileViewService.publish(actors);

        ArgumentCaptor<ProfileViewEventDto> captor = ArgumentCaptor.forClass(ProfileViewEventDto.class);
        verify(redisProfileViewEventPublisher, times(actors.size())).publish(captor.capture());
        List<ProfileViewEventDto> captorDtos = captor.getAllValues();

        captorDtos.forEach(captorDto -> assertThat(captorDto.getReceiverId()).isEqualTo(RECEIVER_ID));
    }

}