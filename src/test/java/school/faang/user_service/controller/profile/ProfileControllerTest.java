package school.faang.user_service.controller.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.dto.event.profile.ProfileViewEvent;
import school.faang.user_service.publisher.profile.ProfileViewEventPublisher;
import school.faang.user_service.validator.profile.ViewProfileValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileControllerTest {

    @Mock
    private ProfileViewEventPublisher eventPublisher;

    @Mock
    private ViewProfileValidator viewProfileValidator;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private ProfileController profileController;

    @Captor
    private ArgumentCaptor<ProfileViewEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void viewProfile_shouldPublishEvent() {
        long userId = 1L;
        long viewerId = 2L;

        when(userContext.getUserId()).thenReturn(userId);

        profileController.viewProfile(viewerId);

        verify(viewProfileValidator).validate(userId, viewerId);
        verify(eventPublisher).publish(eventCaptor.capture());

        ProfileViewEvent event = eventCaptor.getValue();
        assertEquals(userId, event.getUserId());
        assertEquals(viewerId, event.getViewerId());
    }
}
