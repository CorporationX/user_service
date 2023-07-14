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
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceTest {
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

    @Test
    public void testGetInvitationsCallFindAll() {
        Iterable<GoalInvitation> invitations = List.of(new GoalInvitation());
        Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);

        goalInvitationService.getInvitations(new InvitationFilterDto());

        Mockito.verify(goalInvitationRepository, Mockito.times(1)).findAll();
    }


    @Nested
    class FilterTestGroup {
        GoalInvitation goalInvitation1;
        GoalInvitation goalInvitation2;
        GoalInvitation goalInvitation3;
        Iterable<GoalInvitation> invitations;

        @BeforeEach
        public void setUp() {
            User user1 = User.builder()
                    .username("Sus")
                    .id(1)
                    .build();
            User user2 = User.builder()
                    .username("Kek")
                    .id(2)
                    .build();

            goalInvitation1 = new GoalInvitation();
            goalInvitation1.setInviter(user1);
            goalInvitation1.setInvited(user1);
            goalInvitation1.setStatus(RequestStatus.ACCEPTED);

            goalInvitation2 = new GoalInvitation();
            goalInvitation2.setInviter(user2);
            goalInvitation2.setInvited(user2);
            goalInvitation2.setStatus(RequestStatus.ACCEPTED);

            goalInvitation3 = new GoalInvitation();
            goalInvitation3.setInviter(user2);
            goalInvitation3.setInvited(user2);
            goalInvitation3.setStatus(RequestStatus.REJECTED);

            invitations = List.of(goalInvitation1, goalInvitation2, goalInvitation3);
            Mockito.when(goalInvitationRepository.findAll()).thenReturn(invitations);
        }

        @Test
        public void testGetInvitations() {
            InvitationFilterDto filter = new InvitationFilterDto("S", null, 1L, null, RequestStatus.ACCEPTED);

            List<GoalInvitation> result = goalInvitationService.getInvitations(filter);

            assertAll(() -> assertEquals(1, result.size()),
                    () -> assertEquals(goalInvitation1, result.get(0)));
        }

        @Test
        public void testGetInvitations2() {
            InvitationFilterDto filter = new InvitationFilterDto(null, "Kek", null, 2L, RequestStatus.REJECTED);

            List<GoalInvitation> result = goalInvitationService.getInvitations(filter);

            assertAll(() -> assertEquals(1, result.size()),
                    () -> assertEquals(goalInvitation3, result.get(0)));
        }
    }
}