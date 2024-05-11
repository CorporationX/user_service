package school.faang.user_service.filter;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.service.filter.InvitedIdFilter;

import static org.junit.jupiter.api.Assertions.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtendWith(MockitoExtension.class)
public class InvitedIdFilterTest {
    @InjectMocks
    InvitedIdFilter invitedIdFilter;
    InvitationFilterDto invitationFilterDto;
    Data data;

    @BeforeEach
    void prepareInvitationFilterDto() {
        data = new Data();
        invitationFilterDto = data.prepareInvitationFilterDto();
    }


    @Test
    void testIsApplicableFalse() {
        invitationFilterDto.setInvitedId(null);
        assertFalse(invitedIdFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testIsApplicableTrue() {
        assertTrue(invitedIdFilter.isApplicable(invitationFilterDto));
    }

    @Test
    void testApplyWithGoalInvitation() {
        assertEquals(data.prepareGoalInvitationStream().toList().size(), invitedIdFilter.apply(data.prepareGoalInvitationStream(),
                invitationFilterDto).toList().size());
    }

    @Test
    void testApplyWithoutGoalInvitation() {
        invitationFilterDto.setInvitedId(22L);
        assertEquals(0, invitedIdFilter.apply(data.prepareGoalInvitationStream(), invitationFilterDto).toList().size());
    }
}
