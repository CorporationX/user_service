package school.faang.user_service.service.goal.filter_goalinvitation;

import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationStatusFilterTest extends SetUpFiltresTest {
    @InjectMocks
    GoalInvitationStatusFilter goalInvitationStatusFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assert.assertEquals(true, goalInvitationStatusFilter.isApplicable(filterStatusDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assert.assertEquals(false, goalInvitationStatusFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assert.assertEquals(true, goalInvitationStatusFilter.isApplicable(filterStatusDto));
        Assert.assertEquals(true, goalInvitationStatusFilter.apply(goalInvitation, filterStatusDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assert.assertEquals(true, goalInvitationStatusFilter.isApplicable(filterStatusDto));
        Assert.assertEquals(false, goalInvitationStatusFilter.apply(goalInvitation, filterInviterNamePatternDto));
    }
}