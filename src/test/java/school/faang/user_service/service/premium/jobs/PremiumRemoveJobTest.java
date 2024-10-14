package school.faang.user_service.service.premium.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.entity.premium.Premium;
import school.faang.user_service.service.premium.PremiumService;
import school.faang.user_service.service.premium.async.AsyncPremiumService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static school.faang.user_service.util.premium.PremiumFabric.buildPremiums;

@ExtendWith(MockitoExtension.class)
class PremiumRemoveJobTest {
    private static final int BATCH_SIZE = 5;
    private static final int NUMBER_OF_PREMIUMS = 15;

    @Mock
    private PremiumService premiumService;

    @Mock
    private AsyncPremiumService asyncPremiumService;

    @InjectMocks
    private PremiumRemoveJob premiumRemoveJob;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(premiumRemoveJob, "batchSize", BATCH_SIZE);
    }

    @Test
    @DisplayName("Premium remove job successful execute")
    void testExecute() {
        List<Premium> premiums = buildPremiums(NUMBER_OF_PREMIUMS);
        when(premiumService.findAllByEndDateBefore(any(LocalDateTime.class))).thenReturn(premiums);
        premiumRemoveJob.execute(mock(JobExecutionContext.class));

        verify(asyncPremiumService, times(NUMBER_OF_PREMIUMS / BATCH_SIZE))
                .deleteAllPremiumsByIdAsync(anyList());
    }
}