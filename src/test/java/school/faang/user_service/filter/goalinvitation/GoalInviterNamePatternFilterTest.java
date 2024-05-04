package school.faang.user_service.filter.goalinvitation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInviterNamePatternFilterTest extends SetUpFiltersTest {
    @InjectMocks
    GoalInviterNamePatternFilter goalInviterNamePatternFilter;
    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assertions.assertTrue(goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assertions.assertFalse(goalInviterNamePatternFilter.isApplicable(filterInvitedNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assertions.assertTrue(goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
        Assertions.assertTrue(goalInviterNamePatternFilter.apply(goalInvitation, filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assertions.assertTrue(goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
        Assertions.assertFalse(goalInviterNamePatternFilter.apply(goalInvitation, filterInvitedNamePatternDto));
    }
}