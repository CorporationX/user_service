package school.faang.user_service.service.goal.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StatusFilterTest {

    @InjectMocks
    private RequestStatusFilter requestStatusFilter;

    private InvitationFilterDto invitationFilterDto;

    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        testData = new TestData();
        invitationFilterDto = testData.prepareInvitationFilterDto();
    }

    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setStatus(null);
        assertFalse(requestStatusFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(requestStatusFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(1, requestStatusFilter.apply(testData.prepareGoalInvitationList().get(1),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setStatus(RequestStatus.PENDING);
        assertEquals(0, requestStatusFilter.apply(testData.prepareGoalInvitationList().get(0), invitationFilterDto).toList().size());
    }
}
