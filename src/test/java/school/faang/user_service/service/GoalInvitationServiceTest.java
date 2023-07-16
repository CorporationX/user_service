package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
    @Mock
    List<GoalInvitation> invitations;

    @Mock
    GoalInvitationMapper goalInvitationMapper;

    @Mock
    List<InvitationFilter> invitationFilters;

    @Mock
    GoalInvitationRepository goalInvitationRepository;

    @InjectMocks
    GoalInvitationService goalInvitationService;

    @Test
    public void testGetInvitationsThrowIllegalArgsExc() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> goalInvitationService.getInvitations(new InvitationFilterDto()));
        assertEquals("Invalid request. Goal invitation not found.", e.getMessage());
    }

    @Nested
    class positiveTestGroup {
        @BeforeEach
        public void setUp() {
            Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

            goalInvitationService.getInvitations(new InvitationFilterDto());
        }

        @Test
        public void testGetInvitationsCallFindAll() {
            Mockito.verify(goalInvitationRepository, Mockito.times(1)).findAll();
        }

        @Test
        public void testGetInvitationsCallIsEmpty() {
            Mockito.verify(invitations, Mockito.times(1)).isEmpty();
        }

        @Test
        public void testGetInvitationsCallStream1() {
            Mockito.verify(invitationFilters, Mockito.times(1)).stream();
        }

        @Test
        public void testGetInvitationsCallStream() {
            Mockito.verify(invitations, Mockito.times(1)).stream();
        }
    }
}