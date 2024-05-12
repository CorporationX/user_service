package school.faang.user_service.service.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RequestStatusFilterTest {
    @InjectMocks
    private RequestStatusFilter requestStatusFilter;
    private InvitationFilterDto invitationFilterDto;
    private TestData testData;

    @BeforeEach
    void prepareInvitationFilterDto() {
        invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setInvitedId(2L);
        invitationFilterDto.setInviterId(1L);
        invitationFilterDto.setInviterNamePattern("John");
        invitationFilterDto.setInvitedNamePattern("Mike");
        invitationFilterDto.setStatus(RequestStatus.ACCEPTED);
        testData = new TestData();
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
        assertEquals(testData.prepareGoalInvitationStream().toList().size(), requestStatusFilter.apply(testData.prepareGoalInvitationStream(),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setStatus(RequestStatus.PENDING);
        assertEquals(0, requestStatusFilter.apply(testData.prepareGoalInvitationStream(), invitationFilterDto).toList().size());
    }
}
