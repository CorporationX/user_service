package school.faang.user_service.filter.goalinvitation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInviterIdFilterTest extends SetUpFiltersTest {
    @InjectMocks
    GoalInviterIdFilter goalInviterIdFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assertions.assertTrue(goalInviterIdFilter.isApplicable(filterInviterIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assertions.assertFalse(goalInviterIdFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assertions.assertTrue(goalInviterIdFilter.isApplicable(filterInviterIdDto));
        Assertions.assertTrue(goalInviterIdFilter.apply(goalInvitation, filterInviterIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assertions.assertTrue(goalInviterIdFilter.isApplicable(filterInviterIdDto));
        Assertions.assertFalse(goalInviterIdFilter.apply(goalInvitation, filterUserIdDto));
    }
}