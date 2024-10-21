package school.faang.user_service.service.user.view.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import school.faang.user_service.aspect.redis.ProfileViewEventPublisherToRedis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileViewPublishJobTest {
    @Mock
    private ProfileViewEventPublisherToRedis profileViewEventPublisherRedis;

    @InjectMocks
    private ProfileViewPublishJob profileViewPublishJob;

    @Test
    @DisplayName("Given true when check profile views then not execute publishAllProfileViewEvents")
    void testExecuteListIsEmptyTrue() {
        when(profileViewEventPublisherRedis.analyticEventsIsEmpty()).thenReturn(true);
        profileViewPublishJob.execute(mock(JobExecutionContext.class));

        verify(profileViewEventPublisherRedis, never()).publishAllEvents();
    }

    @Test
    @DisplayName("Given false when check profile views then execute publishAllProfileViewEvents")
    void testExecuteListIsEmptyFalse() {
        when(profileViewEventPublisherRedis.analyticEventsIsEmpty()).thenReturn(false);
        profileViewPublishJob.execute(mock(JobExecutionContext.class));

        verify(profileViewEventPublisherRedis).publishAllEvents();
    }
}