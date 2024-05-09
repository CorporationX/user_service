package school.faang.user_service.filter.goalinvitation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInvitationStatusFilterTest extends SetUpFiltersTest {
    @InjectMocks
    GoalInvitationStatusFilter goalInvitationStatusFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assertions.assertTrue(goalInvitationStatusFilter.isApplicable(filterStatusDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assertions.assertFalse(goalInvitationStatusFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assertions.assertTrue(goalInvitationStatusFilter.isApplicable(filterStatusDto));
        Assertions.assertTrue(goalInvitationStatusFilter.apply(goalInvitation, filterStatusDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assertions.assertTrue(goalInvitationStatusFilter.isApplicable(filterStatusDto));
        Assertions.assertFalse(goalInvitationStatusFilter.apply(goalInvitation, filterInviterNamePatternDto));
    }
}