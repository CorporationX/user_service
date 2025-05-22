package school.faang.user_service.service.goal;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.configuration.goals.properties.GoalProperties;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.dto.goal.SortOption;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.entity.goal.QGoalInvitation;
import school.faang.user_service.mapper.goal.GoalInvitationMapperImpl;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.user.UserRepositoryAdapter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalInvitationServiceImplTest {

    @Spy
    private GoalInvitationMapperImpl goalInvitationMapper;
    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private GoalRepositoryAdapter goalRepositoryAdapter;
    @Mock
    private GoalInvitationRepository goalInvitationRepository;
    @Mock
    private GoalProperties goalProperties;
    @Mock
    private InvitationBoolBuilderConstructor invitationBoolBuilderConstructor;
    @Captor
    private ArgumentCaptor<List<GoalInvitation>> goalInvitationsCaptor;

    @InjectMocks
    private GoalInvitationServiceImpl goalInvitationService;

    private GoalInvitationDto goalInvitationDto;
    private User user;
    private User anotherUser;
    private Goal goal;
    private GoalInvitation goalInvitation;
    private GoalInvitation completeGoalInvitation;
    private GoalInvitationDto completeGoalInvitationDto;
    private final QGoalInvitation q = QGoalInvitation.goalInvitation;

    @BeforeEach
    void setUp() {
        goalInvitationDto = GoalInvitationDto.builder()
                .inviterId(1L)
                .invitedId(2L)
                .goalId(1L)
                .build();

        user = User.builder()
                .id(goalInvitationDto.getInviterId())
                .username("username")
                .goals(new ArrayList<>())
                .receivedGoalInvitations(new ArrayList<>())
                .build();

        anotherUser = User.builder()
                .id(goalInvitationDto.getInvitedId())
                .username("username")
                .goals(new ArrayList<>())
                .receivedGoalInvitations(new ArrayList<>())
                .build();

        goal = Goal.builder()
                .id(goalInvitationDto.getGoalId())
                .title("goal title")
                .description("goal description")
                .invitations(new ArrayList<>())
                .users(new ArrayList<>())
                .build();

        goalInvitation = GoalInvitation.builder()
                .inviter(user)
                .invited(anotherUser)
                .goal(goal)
                .status(RequestStatus.PENDING)
                .build();

        completeGoalInvitation = GoalInvitation.builder()
                .id(1L)
                .inviter(user)
                .invited(anotherUser)
                .goal(goal)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        completeGoalInvitationDto = GoalInvitationDto.builder()
                .id(1L)
                .inviterId(user.getId())
                .invitedId(anotherUser.getId())
                .goalId(goal.getId())
                .build();
    }

    @Test
    void testCreateInvitation_whenValidDtoPassed_thenReturnGoalInvitation() {
        when(userRepositoryAdapter.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userRepositoryAdapter.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalRepositoryAdapter.findById(goalInvitationDto.getGoalId())).thenReturn(goal);
        when(goalInvitationMapper.toGoalInvitation(user, anotherUser, goal)).thenReturn(goalInvitation);
        when(goalInvitationMapper.toGoalInvitationDto(completeGoalInvitation)).thenReturn(completeGoalInvitationDto);
        when(goalInvitationRepository.save(any())).thenReturn(completeGoalInvitation);

        GoalInvitationDto savedInvitationDto = goalInvitationService.createInvitation(goalInvitationDto);

        assertTrue(goal.getInvitations().contains(goalInvitation));
        assertEquals(savedInvitationDto, completeGoalInvitationDto);
        verify(goalInvitationRepository, times(1)).save(any());
    }

    @Test
    void testCreateInvitation_whenGoalInvitationDtoIsNull_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> goalInvitationService.createInvitation(null));
    }

    @Test
    void testCreateInvitation_whenInviterAndInvitedAreTheSame_thenThrowIllegalArgumentException() {
        when(userRepositoryAdapter.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userRepositoryAdapter.findById(goalInvitationDto.getInvitedId())).thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        verify(goalInvitationRepository, times(0)).save(any());
    }

    @Test
    void testCreateInvitation_whenGoalIsNotFound_thenThrowIllegalArgumentException() {
        when(userRepositoryAdapter.findById(goalInvitationDto.getInviterId())).thenReturn(user);
        when(userRepositoryAdapter.findById(goalInvitationDto.getInvitedId())).thenReturn(anotherUser);
        when(goalRepositoryAdapter.findById(goalInvitationDto.getGoalId())).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.createInvitation(goalInvitationDto));
        verify(goalInvitationRepository, times(0)).save(any());
    }

    @Test
    void testAcceptGoalInvitation() {
        anotherUser.setGoals(new ArrayList<>());
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(goalProperties.getMaxActiveGoals()).thenReturn(3L);

        goalInvitationService.acceptGoalInvitation(any(Long.class));
        assertEquals(RequestStatus.ACCEPTED, completeGoalInvitation.getStatus());
        assertTrue(goal.getUsers().contains(anotherUser));
        assertTrue(anotherUser.getGoals().contains(goal));
    }

    @Test
    void testAcceptGoalInvitation_whenGoalInvitationIsNotFound_thenThrowIllegalArgumentException() {
        when(goalInvitationRepository.findById(any(Long.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testAcceptGoalInvitation_whenUserHasMoreThanMaxActiveGoals_thenThrowIllegalArgumentException() {
        anotherUser.setGoals(new ArrayList<>(List.of(goal, goal, goal, goal)));
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(goalProperties.getMaxActiveGoals()).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testAcceptGoalInvitation_whenUserAlreadyHasInvitedGoal_thenThrowIllegalArgumentException() {
        anotherUser.setGoals(List.of(goal));
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));
        when(goalProperties.getMaxActiveGoals()).thenReturn(3L);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testRejectGoalInvitation_whenGoalInvitationIsNotFound_thenThrowIllegalArgumentException() {
        when(goalInvitationRepository.findById(any(Long.class))).thenThrow(IllegalArgumentException.class);

        assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(any(Long.class)));
    }

    @Test
    void testRejectGoalInvitation() {
        when(goalInvitationRepository.findById(any(Long.class))).thenReturn(Optional.of(completeGoalInvitation));

        goalInvitationService.rejectGoalInvitation(any(Long.class));
        assertEquals(RequestStatus.REJECTED, completeGoalInvitation.getStatus());
    }

    @Test
    void testGetAllInvitations_whenNullPassed_thenThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> goalInvitationService.getAllInvitations(null));
    }

    @Test
    void testGetAllInvitations() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setSize(10);
        invitationFilterDto.setOffset(0);
        when(invitationBoolBuilderConstructor.getQueryBooleanBuilder(any(InvitationFilterDto.class)))
                .thenReturn(new BooleanBuilder());
        when(goalInvitationRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(completeGoalInvitation)));

        List<GoalInvitationDto> goalInvitations = goalInvitationService.getAllInvitations(invitationFilterDto);

        assertEquals(1, goalInvitations.size());
        assertEquals(completeGoalInvitation.getId(), goalInvitations.get(0).getId());
    }

    @Test
    void testGetAllInvitations_whenSortOptionIsPassedForFiltering_thenReturnSortedInvitations() {
        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
        invitationFilterDto.setSize(10);
        invitationFilterDto.setOffset(0);
        invitationFilterDto.setSort(SortOption.STATUS);

        GoalInvitation acceptedInvitation = new GoalInvitation();
        acceptedInvitation.setId(1L);
        acceptedInvitation.setInviter(user);
        acceptedInvitation.setInvited(anotherUser);
        acceptedInvitation.setGoal(goal);
        acceptedInvitation.setStatus(RequestStatus.ACCEPTED);
        acceptedInvitation.setCreatedAt(LocalDateTime.now());
        acceptedInvitation.setUpdatedAt(LocalDateTime.now());

        when(invitationBoolBuilderConstructor.getQueryBooleanBuilder(any(InvitationFilterDto.class)))
                .thenReturn(new BooleanBuilder());
        when(goalInvitationRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(completeGoalInvitation, acceptedInvitation)));

        List<GoalInvitationDto> goalInvitations = goalInvitationService.getAllInvitations(invitationFilterDto);

        assertEquals(2, goalInvitations.size());
        assertEquals(RequestStatus.PENDING, goalInvitations.get(0).getStatus());
    }


    @Test
    void getAllInvitations_shouldApplyFiltersAndReturnMappedDtos() {
        InvitationFilterDto filterDto = new InvitationFilterDto(
                1L,
                2L,
                RequestStatus.PENDING,
                LocalDateTime.of(2026, 5, 1, 0, 0),
                LocalDateTime.of(2025, 1, 1, 0, 0),
                0,
                10,
                null
        );

        GoalInvitation goalInvitationFiltered = GoalInvitation.builder()
                .id(1L)
                .inviter(user)
                .invited(anotherUser)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .goal(goal)
                .build();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(q.inviter.id.eq(filterDto.getInviterId()));
        builder.and(q.invited.id.eq(filterDto.getInvitedId()));
        builder.and(q.status.eq(filterDto.getStatus()));
        builder.and(q.createdAt.loe(filterDto.getCreatedBefore()));
        builder.and(q.createdAt.goe(filterDto.getCreatedAfter()));

        List<GoalInvitation> entities = List.of(goalInvitationFiltered);
        List<GoalInvitationDto> expectedDtos = List.of(goalInvitationMapper.toGoalInvitationDto(goalInvitationFiltered));
        Page<GoalInvitation> page = new PageImpl<>(entities);

        when(invitationBoolBuilderConstructor.getQueryBooleanBuilder(filterDto)).thenReturn(builder);
        when(goalInvitationRepository.findAll(eq(builder), any(PageRequest.class))).thenReturn(page);

        List<GoalInvitationDto> result = goalInvitationService.getAllInvitations(filterDto);

        assertEquals(expectedDtos, result);
        verify(invitationBoolBuilderConstructor).getQueryBooleanBuilder(filterDto);
        verify(goalInvitationRepository).findAll(eq(builder), eq(PageRequest.of(0, 10)));
        verify(goalInvitationMapper, times(1)).toGoalInvitations(goalInvitationsCaptor.capture());
        List<GoalInvitation> allInvitations = goalInvitationsCaptor.getValue();
        assertEquals(entities.size(), allInvitations.size());
    }

    @Test
    void getAllInvitations_withSort_shouldReturnSortedDtos() {
        InvitationFilterDto filterDto = new InvitationFilterDto();
        filterDto.setOffset(0);
        filterDto.setSize(10);
        filterDto.setCreatedAfter(LocalDateTime.of(2025, 1, 1, 0, 0));
        filterDto.setCreatedBefore(LocalDateTime.of(2026, 1, 1, 0, 0));
        filterDto.setSort(SortOption.STATUS);

        GoalInvitation goalInvitationPendingStatus = GoalInvitation.builder()
                .inviter(user)
                .invited(anotherUser)
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .goal(goal)
                .build();

        GoalInvitation goalInvitationAcceptedStatus = GoalInvitation.builder()
                .inviter(user)
                .invited(anotherUser)
                .status(RequestStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .goal(goal)
                .build();

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(q.createdAt.loe(filterDto.getCreatedBefore()));
        builder.and(q.createdAt.goe(filterDto.getCreatedAfter()));

        List<GoalInvitation> unsortedInvitations = List.of(goalInvitationAcceptedStatus, goalInvitationPendingStatus);
        Page<GoalInvitation> page = new PageImpl<>(unsortedInvitations);

        List<GoalInvitation> sortedInvitations = unsortedInvitations.stream()
                .sorted(Comparator.comparing(GoalInvitation::getStatus))
                .collect(Collectors.toList());

        GoalInvitationDto dto1 = goalInvitationMapper.toGoalInvitationDto(sortedInvitations.get(0));
        GoalInvitationDto dto2 = goalInvitationMapper.toGoalInvitationDto(sortedInvitations.get(1));

        List<GoalInvitationDto> expectedDtos = List.of(dto1, dto2);
        when(invitationBoolBuilderConstructor.getQueryBooleanBuilder(filterDto)).thenReturn(builder);
        when(goalInvitationRepository.findAll(eq(builder), any(PageRequest.class))).thenReturn(page);

        List<GoalInvitationDto> result = goalInvitationService.getAllInvitations(filterDto);

        assertEquals(expectedDtos, result);
        verify(invitationBoolBuilderConstructor).getQueryBooleanBuilder(filterDto);
        verify(goalInvitationRepository).findAll(eq(builder), eq(PageRequest.of(0, 10)));
        verify(goalInvitationMapper, times(1)).toGoalInvitations(goalInvitationsCaptor.capture());
        List<GoalInvitation> allInvitations = goalInvitationsCaptor.getValue();
        assertEquals(sortedInvitations.size(), allInvitations.size());
    }
}