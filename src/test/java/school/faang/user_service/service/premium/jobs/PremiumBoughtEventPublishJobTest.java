package school.faang.user_service.service.premium.jobs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import school.faang.user_service.service.premium.PremiumBoughtEventService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PremiumBoughtEventPublishJobTest {
    @Mock
    private PremiumBoughtEventService premiumBoughtEventService;

    @InjectMocks
    private PremiumBoughtEventPublishJob premiumBoughtEventPublishJob;

    @Test
    @DisplayName("Given true when check profile views then not execute publishAllPremiumBoughtEvents")
    void testExecuteListIsEmptyTrue() {
        when(premiumBoughtEventService.premiumBoughtEventDtosIsEmpty()).thenReturn(true);
        premiumBoughtEventPublishJob.execute(mock(JobExecutionContext.class));

        verify(premiumBoughtEventService, never()).publishAllPremiumBoughtEvents();
    }

    @Test
    @DisplayName("Given false when check profile views then execute publishAllPremiumBoughtEvents")
    void testExecuteListIsEmptyFalse() {
        when(premiumBoughtEventService.premiumBoughtEventDtosIsEmpty()).thenReturn(false);
        premiumBoughtEventPublishJob.execute(mock(JobExecutionContext.class));

        verify(premiumBoughtEventService).publishAllPremiumBoughtEvents();
    }
}