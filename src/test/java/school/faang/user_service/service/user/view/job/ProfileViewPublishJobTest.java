package school.faang.user_service.service.user.view.job;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import school.faang.user_service.service.user.view.ProfileViewService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileViewPublishJobTest {
    @Mock
    private ProfileViewService profileViewService;

    @InjectMocks
    private ProfileViewPublishJob profileViewPublishJob;

    @Test
    @DisplayName("Given true when check profile views then not execute publishAllProfileViewEvents")
    void testExecuteListIsEmptyTrue() {
        when(profileViewService.profileViewEventDtosIsEmpty()).thenReturn(true);
        profileViewPublishJob.execute(mock(JobExecutionContext.class));

        verify(profileViewService, never()).publishAllProfileViewEvents();
    }

    @Test
    @DisplayName("Given false when check profile views then execute publishAllProfileViewEvents")
    void testExecuteListIsEmptyFalse() {
        when(profileViewService.profileViewEventDtosIsEmpty()).thenReturn(false);
        profileViewPublishJob.execute(mock(JobExecutionContext.class));

        verify(profileViewService).publishAllProfileViewEvents();
    }
}