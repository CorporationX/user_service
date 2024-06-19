package school.faang.user_service.service.goal.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class InviterNamePatternFilterTest {

    @InjectMocks
    private InviterNamePatternFilter inviterNamePatternFilter;

    private InvitationFilterDto invitationFilterDto;

    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        testData = new TestData();
        invitationFilterDto = testData.prepareInvitationFilterDto();
    }

    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setInviterNamePattern(null);
        assertFalse(inviterNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(inviterNamePatternFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(1, inviterNamePatternFilter.apply(testData.prepareGoalInvitationList().get(1),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        assertEquals(0, inviterNamePatternFilter.apply(testData.prepareGoalInvitationList().get(0), invitationFilterDto).toList().size());
    }
}
