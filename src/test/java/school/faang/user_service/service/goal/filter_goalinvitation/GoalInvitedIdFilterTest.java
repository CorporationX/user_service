package school.faang.user_service.service.goal.filter_goalinvitation;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInvitedIdFilterTest extends SetUpFiltresTest {
    @InjectMocks
    GoalInvitedIdFilter goalInvitedIdFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assert.assertEquals(true, goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assert.assertEquals(false, goalInvitedIdFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assert.assertEquals(true, goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
        Assert.assertEquals(true, goalInvitedIdFilter.apply(goalInvitation, filterInvitedIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assert.assertEquals(true, goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
        Assert.assertEquals(false, goalInvitedIdFilter.apply(goalInvitation, filterUserIdDto));
    }
}