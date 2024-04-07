package school.faang.user_service.service.goal.filter_goalinvitation;

import org.junit.Assert;
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
        Assert.assertEquals(true, goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assert.assertEquals(false, goalInviterNamePatternFilter.isApplicable(filterInvitedNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assert.assertEquals(true, goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
        Assert.assertEquals(true, goalInviterNamePatternFilter.apply(goalInvitation, filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assert.assertEquals(true, goalInviterNamePatternFilter.isApplicable(filterInviterNamePatternDto));
        Assert.assertEquals(false, goalInviterNamePatternFilter.apply(goalInvitation, filterInvitedNamePatternDto));
    }
}