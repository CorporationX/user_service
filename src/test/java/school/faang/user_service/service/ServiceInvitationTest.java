//package school.faang.user_service.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.goal.GoalInvitationDto;
//import school.faang.user_service.dto.goal.InvitationFilterDto;
//import school.faang.user_service.entity.RequestStatus;
//import school.faang.user_service.entity.goal.Goal;
//import school.faang.user_service.entity.goal.GoalInvitation;
//import school.faang.user_service.filters.filtersForGoalInvitation.GoalInvitationFilter;
//import school.faang.user_service.mapper.GoalInvitationMapperImpl;
//import school.faang.user_service.mapper.UserMapper;
//import school.faang.user_service.repository.UserRepository;
//import school.faang.user_service.repository.goal.GoalInvitationRepository;
//import school.faang.user_service.repository.goal.GoalRepository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//
//public class ServiceInvitationTest {
//    @Mock
//    private GoalRepository goalRepository;
//    @Mock
//    private UserRepository userRepository;
//    @Mock
//    GoalInvitationRepository goalInvitationRepository;
//    @Mock
//    GoalInvitation goalInvitation;
//    @Spy
//    GoalInvitationMapperImpl goalInvitationMapper;
//    @InjectMocks
//    private GoalInvitationService goalInvitationService;
//
//    @Test
//    void createGoalInvitation() {
//        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
//        goalInvitationDto.setInvitedUserId(2L);
//        goalInvitationDto.setInviterId(1L);
//
//        when(goalInvitationRepository.existsById(1L)).thenReturn(true);
//
//        goalInvitationService.createInvitation(goalInvitationDto);
//        verify(goalInvitationRepository).save(any());
//    }
//
//    @Test
//    void acceptGoalInvitation() {
//        // Необходимо дописать сюда реализацию метода
//    }
//
// //   @Test
////    void rejectGoalInvitation() {
////        long id = 1l;
////        GoalInvitation goalInvitation = new GoalInvitation(invitationDto.getId(), new Goal(), inviter, invited, invitationDto.getStatus());
////        GoalInvitationDto goalInvitationDto = new GoalInvitationDto();
////        goalInvitationDto.setStatus(RequestStatus.valueOf("REJECTED"));
////
////        when(goalRepository.existsById(any())).thenReturn(true);
////
////        when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
////
////        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(goalInvitationDto);
////
////        GoalInvitationDto gg = goalInvitationService.rejectGoalInvitation(id);
////        assertEquals(goalInvitationDto.getStatus(), gg.getStatus());
////    }
//
////    @BeforeEach
////    void getInvitations() {
////        UserMapper userMapper = Mockito.mock(UserMapper.class);
////        GoalInvitationFilter goalInvitationFilter = Mockito.mock(GoalInvitationFilter.class);
////        List<GoalInvitationFilter> filterList = List.of(goalInvitationFilter);
////        List<GoalInvitation> invitationList = List.of(goalInvitation);
////        Stream<GoalInvitation> gStream = invitationList.stream();
////        InvitationFilterDto invitationFilterDto = new InvitationFilterDto();
////
////        goalInvitationService = new GoalInvitationService(goalInvitationRepository,
////                userRepository, goalRepository,
////                goalInvitationMapper, filterList);
////        when(filterList.get(0).isApplicable(new InvitationFilterDto())).thenReturn(true);
////        when(filterList.get(0).apply(gStream, invitationFilterDto))
////                .thenReturn(invitationList.stream());
////        when(goalInvitationMapper.toDto(goalInvitation)).thenReturn(any());
////
////        goalInvitationService.getInvitations(gStream, invitationFilterDto);
////
////        verify(goalInvitationFilter).isApplicable(new InvitationFilterDto());
////        verify(goalInvitationFilter).apply(gStream, invitationFilterDto);
////        verify(goalInvitationMapper).toDto(goalInvitation);
////    }
//
//}