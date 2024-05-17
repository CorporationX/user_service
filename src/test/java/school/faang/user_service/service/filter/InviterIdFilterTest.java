package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InviterIdFilterTest {
    @InjectMocks
    private InviterIdFilter inviterIdFilter;
    private InvitationFilterDto invitationFilterDto;
    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        testData = new TestData();
        invitationFilterDto = testData.prepareInvitationFilterDto();
    }


    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setInviterId(null);
        assertFalse(inviterIdFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(inviterIdFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(1, inviterIdFilter.apply(testData.prepareGoalInvitationList().stream(),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setInviterId(200L);
        assertEquals(0, inviterIdFilter.apply(testData.prepareGoalInvitationList().stream(), invitationFilterDto).toList().size());
    }
}
