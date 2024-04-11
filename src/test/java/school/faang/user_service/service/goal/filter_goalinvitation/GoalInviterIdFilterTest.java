package school.faang.user_service.service.goal.filter_goalinvitation;

import org.junit.Assert;
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
        Assert.assertEquals(true, goalInviterIdFilter.isApplicable(filterInviterIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assert.assertEquals(false, goalInviterIdFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assert.assertEquals(true, goalInviterIdFilter.isApplicable(filterInviterIdDto));
        Assert.assertEquals(true, goalInviterIdFilter.apply(goalInvitation, filterInviterIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assert.assertEquals(true, goalInviterIdFilter.isApplicable(filterInviterIdDto));
        Assert.assertEquals(false, goalInviterIdFilter.apply(goalInvitation, filterUserIdDto));
    }
}