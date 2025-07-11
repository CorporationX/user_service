package school.faang.user_service.policy.goal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestDefaultGoalPolicyUtilsDenyIfNotSelfAndMentee {
    public static final long CURRENT_USER_ID = 5L;
    @InjectMocks
    DefaultGoalPolicyUtils utils;

    @Test
    void shouldNotDenyWhenSelf() {
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotSelfAndMentee(
                CURRENT_USER_ID,
                List.of(CURRENT_USER_ID, 42L, 99L),
                false,
                deny
        );

        verify(deny, never()).run();
    }

    void shouldNotDenyWhenMentee() {
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotSelfAndMentee(
                CURRENT_USER_ID,
                List.of(1L, 2L, 3L),
                true,
                deny
        );
        verify(deny, never()).run();
    }

    @Test
    void shouldDenyWhenNotSelfAndNotMentee() {
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotSelfAndMentee(
                CURRENT_USER_ID,
                List.of(1L, 2L, 3L),
                false,
                deny
        );

        verify(deny, times(1)).run();
    }

    @Test
    void shouldDenyWhenListNullAndNotMentee() {
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotSelfAndMentee(
                CURRENT_USER_ID,
                null,
                false,
                deny
        );

        verify(deny, times(1)).run();
    }

    @Test
    void shouldNotDenyWhenListNullAndMentee() {
        Runnable deny = mock(Runnable.class);

        utils.denyIfNotSelfAndMentee(
                CURRENT_USER_ID,
                null,
                true,
                deny
        );

        verify(deny, never()).run();
    }
}
