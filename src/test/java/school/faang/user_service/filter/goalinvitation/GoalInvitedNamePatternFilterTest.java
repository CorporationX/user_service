package school.faang.user_service.filter.goalinvitation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInvitedNamePatternFilterTest extends SetUpFiltersTest {
    @InjectMocks
    GoalInvitedNamePatternFilter goalInvitedNamePatternFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assertions.assertTrue(goalInvitedNamePatternFilter.isApplicable(filterInvitedNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assertions.assertFalse(goalInvitedNamePatternFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assertions.assertTrue(goalInvitedNamePatternFilter.isApplicable(filterInvitedNamePatternDto));
        Assertions.assertTrue(goalInvitedNamePatternFilter.apply(goalInvitation, filterInvitedNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assertions.assertTrue(goalInvitedNamePatternFilter.isApplicable(filterInvitedNamePatternDto));
        Assertions.assertFalse(goalInvitedNamePatternFilter.apply(goalInvitation, filterInviterNamePatternDto));
    }
}