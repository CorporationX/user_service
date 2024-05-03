package school.faang.user_service.filter.goalinvitation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GoalInvitedIdFilterTest extends SetUpFiltersTest {
    @InjectMocks
    GoalInvitedIdFilter goalInvitedIdFilter;

    @Test
    @DisplayName("Testing if the filters are applicable")
    public void testFiltersAreApplicable() {
        Assertions.assertTrue(goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreNotApplicable() {
        Assertions.assertFalse(goalInvitedIdFilter.isApplicable(filterInviterNamePatternDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersAreApply() {
        Assertions.assertTrue(goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
        Assertions.assertTrue(goalInvitedIdFilter.apply(goalInvitation, filterInvitedIdDto));
    }

    @Test
    @DisplayName("Check if the filters are applied correctly")
    public void testFiltersNotAreApply() {
        Assertions.assertTrue(goalInvitedIdFilter.isApplicable(filterInvitedIdDto));
        Assertions.assertFalse(goalInvitedIdFilter.apply(goalInvitation, filterUserIdDto));
    }
}