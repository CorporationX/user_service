//package school.faang.user_service.service.service.mentorship;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
//import school.faang.user_service.dto.mentorship.RejectionDto;
//import school.faang.user_service.entity.MentorshipRequest;
//import school.faang.user_service.entity.RequestStatus;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverIdFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterIdFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
//import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
//import school.faang.user_service.repository.UserRepository;
//import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
//import school.faang.user_service.service.mentorship.impl.MentorshipRequestServiceImpl;
//import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;
//import school.faang.user_service.validator.user.UserValidator;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@ExtendWith(MockitoExtension.class)
//public class MentorshipRequestServiceTest {
//    private MentorshipRequestDto mrDtoWithId1;
//    private MentorshipRequestDto mrDtoWithId2;
//    private MentorshipRequestDto mrDtoWithId3;
//    private MentorshipRequestDto mrDtoWithId4;
//    private MentorshipRequest mrEntityWithId1;
//    private MentorshipRequest mrEntityWithId2;
//    private MentorshipRequest mrEntityWithId3;
//    private MentorshipRequest mrEntityWithId4;
//
//    private RejectionDto rejectionDtoForMr1;
//
//    private Long userId1;
//    private Long userId2;
//    private Long userId3;
//    private Long userId4;
//    private Long userId5;
//    private Long mentorshipRequestId1;
//    private Long mentorshipRequestId2;
//    private Long mentorshipRequestId3;
//    private Long mentorshipRequestId4;
//
//    private User userWithId1;
//    private User userWithId2;
//    private User userWithId3;
//    private User userWithId4;
//    private User userWithId5;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private LocalDateTime firstCreatedAt;
//    private LocalDateTime firstUpdatedAt;
//    private String descriptionOfMRWithId1;
//    private String descriptionOfMRWithId2;
//    private String descriptionOfMRWithId3;
//    private String descriptionOfMRWithId4;
//    private String rejectionReasonOfMRWithId1;
//    private String rejectionReasonOfMRWithId2;
//    private String rejectionReasonOfMRWithId3;
//    private String rejectionReasonOfMRWithId4;
//
//
//    @Mock
//    private MentorshipRequestRepository mentorshipRequestRepository;
//
//    @Mock
//    private MentorshipRequestValidator mentorshipRequestValidator;
//
//    @Mock
//    private UserValidator userValidator;
//    @Mock
//    private UserRepository userRepository;
//
//    @Spy
//    private MentorshipRequestMapperImpl mapper;
//
//    @Spy
//    private MentorshipRequestDescriptionFilter descriptionFilter;
//
//    @Spy
//    private MentorshipRequestReceiverIdFilter receiverIdFilter;
//
//    @Spy
//    private MentorshipRequestRequesterIdFilter requesterIdFilter;
//
//    @Spy
//    private MentorshipRequestStatusFilter statusFilter;
//
//    @Spy
//    private List<MentorshipRequestFilter> filters;
//
//    @InjectMocks
//    private MentorshipRequestServiceImpl mentorshipRequestService;
//
//    @Captor
//    private ArgumentCaptor<Long> idCaptor;
//
//    @Captor
//    private ArgumentCaptor<List<Long>> usersIdsCaptor;
//
//    @Captor
//    private ArgumentCaptor<MentorshipRequestDto> mentorshipRequestDtoCaptor;
//
//    @Captor
//    private ArgumentCaptor<MentorshipRequest> mentorshipRequestCaptor;
//
//    @BeforeEach
//    public void init() {
//        userId1 = 1L;
//        userId2 = 2L;
//        userId3 = 3L;
//        userId4 = 4L;
//        userId5 = 5L;
//
//        mentorshipRequestId1 = 1L;
//        mentorshipRequestId2 = 2L;
//        mentorshipRequestId3 = 3L;
//        mentorshipRequestId4 = 4L;
//
//        createdAt = LocalDateTime.of(2022, 1, 1, 12, 0);
//        updatedAt = LocalDateTime.of(2022, 1, 1, 17, 0);
//        firstCreatedAt = LocalDateTime.of(2024, 1, 1, 12, 0);
//        firstUpdatedAt = LocalDateTime.of(2024, 1, 1, 15, 0);
//
//        String userNameUserId1 = "NameOfUserId1";
//        String userNameUserId2 = "NameOfUserId2";
//        String userNameUserId3 = "NameOfUserId3";
//        String userNameUserId4 = "NameOfUserId4";
//        String userNameUserId5 = "NameOfUserId5";
//
//        descriptionOfMRWithId1 = "description of a mentorship request with id 1";
//        descriptionOfMRWithId2 = "description of a mentorship request with id 2";
//        descriptionOfMRWithId3 = "description of a mentorship request with id 3";
//        descriptionOfMRWithId4 = "description of a mentorship request with id 4";
//
//        rejectionReasonOfMRWithId1 = "rejection reason of a mentorship request with id 1";
//        rejectionReasonOfMRWithId2 = "rejection reason of a mentorship request with id 2";
//        rejectionReasonOfMRWithId3 = "rejection reason of a mentorship request with id 3";
//        rejectionReasonOfMRWithId4 = "rejection reason of a mentorship request with id 4";
//
//        userWithId1 = User.builder()
//                .id(userId1)
//                .username(userNameUserId1)
//                .build();
//        userWithId2 = User.builder()
//                .id(userId2)
//                .username(userNameUserId2)
//                .build();
//        userWithId3 = User.builder()
//                .id(userId3)
//                .username(userNameUserId3)
//                .build();
//        userWithId4 = User.builder()
//                .id(userId4)
//                .username(userNameUserId4)
//                .build();
//        userWithId5 = User.builder()
//                .id(userId5)
//                .username(userNameUserId5)
//                .build();
//
//        mrDtoWithId1 = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requesterId(userId3)
//                .receiverId(userId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//        mrDtoWithId2 = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId2)
//                .description(descriptionOfMRWithId2)
//                .requesterId(userId1)
//                .receiverId(userId2)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId2)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//        mrDtoWithId3 = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId3)
//                .description(descriptionOfMRWithId3)
//                .requesterId(userId3)
//                .receiverId(userId4)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId3)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//        mrDtoWithId4 = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId4)
//                .description(descriptionOfMRWithId1)
//                .requesterId(userId4)
//                .receiverId(userId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId4)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//
//
//        mrEntityWithId1 = MentorshipRequest.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requester(userWithId3)
//                .receiver(userWithId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//        mrEntityWithId2 = MentorshipRequest.builder()
//                .id(mentorshipRequestId2)
//                .description(descriptionOfMRWithId2)
//                .requester(userWithId1)
//                .receiver(userWithId2)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId2)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//        mrEntityWithId3 = MentorshipRequest.builder()
//                .id(mentorshipRequestId3)
//                .description(descriptionOfMRWithId3)
//                .requester(userWithId3)
//                .receiver(userWithId4)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId3)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//        mrEntityWithId4 = MentorshipRequest.builder()
//                .id(mentorshipRequestId4)
//                .description(descriptionOfMRWithId4)
//                .requester(userWithId4)
//                .receiver(userWithId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId4)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//
//        rejectionDtoForMr1 = RejectionDto.builder()
//                .id(mentorshipRequestId1)
//                .requesterId(userId3)
//                .receiverId(userId5)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .build();
//
//    }
//
//
//    private MentorshipRequestDto setUp(Long requesterId, Long receiverId, List<Long> listOfUsersIds,
//                                       List<User> listOfUsers, MentorshipRequestDto mentorshipRequestDto) {
//        when(userValidator.validateUsersExistence(listOfUsersIds))
//                .thenReturn(listOfUsers);
//        when(mentorshipRequestRepository.save(mrEntityWithId1))
//                .thenReturn(mrEntityWithId1);
//        return mentorshipRequestService.requestMentorship(requesterId, receiverId, mentorshipRequestDto);
//    }
//
//
//    private void setUpForAcceptRequest() {
//        when(mentorshipRequestValidator.validateMentorshipRequestExistence(mentorshipRequestId1))
//                .thenReturn(mrEntityWithId1);
//        userWithId3.setMentors(new ArrayList<>());
//        mentorshipRequestService.acceptRequest(mentorshipRequestId1);
//    }
//
//    private void setUpForRejectRequest() {
//        when(mentorshipRequestValidator.validateMentorshipRequestExistence(mentorshipRequestId1))
//                .thenReturn(mrEntityWithId1);
//        userWithId3.setMentors(new ArrayList<>());
//        mentorshipRequestService.rejectRequest(mentorshipRequestId1, rejectionDtoForMr1);
//    }
//
//    @Test
//    public void testRequestMentorshipCallsValidateUsersExistenceWithReceiverId5AndRequesterId4() {
//        List<Long> actual;
//        var expected = List.of(userId3, userId5);
//        var listOfUsersIds = List.of(userId3, userId5);
//        var listOfUsers = List.of(userWithId3, userWithId5);
//
//        setUp(userId3, userId5, listOfUsersIds, listOfUsers, mrDtoWithId1);
//
//        verify(userValidator, times(1))
//                .validateUsersExistence(usersIdsCaptor.capture());
//
//        actual = usersIdsCaptor.getValue();
//        assertEquals(expected, actual);
//        verifyNoMoreInteractions(userValidator);
//    }
//
//    @Test
//    public void testRequestMentorshipCallsValidateMentorshipRequestWithSecondDto() {
//        MentorshipRequestDto actual;
//        var expected = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requesterId(userId3)
//                .receiverId(userId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//        var listOfUsersIds = List.of(userId3, userId5);
//        var listOfUsers = List.of(userWithId3, userWithId5);
//
//        setUp(userId3, userId5, listOfUsersIds, listOfUsers, mrDtoWithId1);
//        verify(mentorshipRequestValidator, times(1))
//                .validateMentorshipRequest(mentorshipRequestDtoCaptor.capture());
//
//        actual = mentorshipRequestDtoCaptor.getValue();
//        assertEquals(expected, actual);
//        verifyNoMoreInteractions(mentorshipRequestValidator);
//    }
//
//    @Test
//    public void testThrowsNoSuchElementExceptionWhenValidateUsersExistenceReturnsEmptyList() {
//        var listOfUsersIds = List.of(userId3, userId5);
//        List<User> listOfUsers = Collections.emptyList();
//
//        when(userValidator.validateUsersExistence(listOfUsersIds))
//                .thenReturn(listOfUsers);
//
//        assertThrows(NoSuchElementException.class,
//                () -> mentorshipRequestService.requestMentorship(userId3, userId5, mrDtoWithId1));
//    }
//
//    @Test
//    public void testRequestMentorshipCallsSaveWithFirstEntity() {
//        MentorshipRequest actual;
//        var expected = MentorshipRequest.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requester(userWithId3)
//                .receiver(userWithId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//        var listOfUsersIds = List.of(userId3, userId5);
//        var listOfUsers = List.of(userWithId3, userWithId5);
//
//        setUp(userId3, userId5, listOfUsersIds, listOfUsers, mrDtoWithId1);
//        verify(mentorshipRequestRepository, times(1))
//                .save(mentorshipRequestCaptor.capture());
//
//        actual = mentorshipRequestCaptor.getValue();
//        assertEquals(expected, actual);
//        verifyNoMoreInteractions(mentorshipRequestRepository);
//    }
//
//    @Test
//    public void testRequestMentorshipReturnsFirstEntity() {
//        var expected = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requesterId(userId3)
//                .receiverId(userId5)
//                .status(RequestStatus.PENDING)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//        var listOfUsersIds = List.of(userId3, userId5);
//        var listOfUsers = List.of(userWithId3, userWithId5);
//        var actual = setUp(userId3, userId5, listOfUsersIds, listOfUsers, mrDtoWithId1);
//
//        assertEquals(expected, actual);
//        verifyNoMoreInteractions(userValidator, mentorshipRequestRepository);
//    }
//
//    @Test
//    public void testAcceptMentorshipRequestValidateMentorshipRequestExistenceShouldReceiveMentorshipRequestId1AsArgument() {
//        setUpForAcceptRequest();
//
//        verify(mentorshipRequestValidator, times(1))
//                .validateMentorshipRequestExistence(idCaptor.capture());
//
//        var actual = idCaptor.getValue();
//        var expected = mentorshipRequestId1;
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testAcceptMentorshipRequestValidateMentorShouldReceiveMrEntityWithId1AsArgument() {
//        setUpForAcceptRequest();
//
//        verify(mentorshipRequestValidator, times(1))
//                .validateMentor(mentorshipRequestCaptor.capture());
//
//        var actual = mentorshipRequestCaptor.getValue();
//        var expected = mrEntityWithId1;
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testAcceptMentorshipSaveShouldReceiveMrEntityWithId1AndStatusACCEPTEDAAndMentorUserWithId5AsArgument() {
//        setUpForAcceptRequest();
//
//        verify(mentorshipRequestRepository, times(1))
//                .save(mentorshipRequestCaptor.capture());
//
//        var actual = mentorshipRequestCaptor.getValue();
//        var expected = MentorshipRequest.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requester(userWithId3)
//                .receiver(userWithId5)
//                .status(RequestStatus.ACCEPTED)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testRejectRequestMentorshipRequestValidateMentorshipRequestExistenceShouldReceiveMentorshipRequestId1AsArgument() {
//        setUpForRejectRequest();
//
//        verify(mentorshipRequestValidator, times(1))
//                .validateMentorshipRequestExistence(idCaptor.capture());
//
//        var actual = idCaptor.getValue();
//        var expected = mentorshipRequestId1;
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testRejectMentorshipSaveShouldReceiveMrEntityWithId1AndStatusREJECTEDAndMentorUserWithId5AsArgument() {
//        setUpForRejectRequest();
//
//        verify(mentorshipRequestRepository, times(1))
//                .save(mentorshipRequestCaptor.capture());
//
//        var actual = mentorshipRequestCaptor.getValue();
//        var expected = MentorshipRequest.builder()
//                .id(mentorshipRequestId1)
//                .description(descriptionOfMRWithId1)
//                .requester(userWithId3)
//                .receiver(userWithId5)
//                .status(RequestStatus.REJECTED)
//                .rejectionReason(rejectionReasonOfMRWithId1)
//                .createdAt(firstCreatedAt)
//                .updatedAt(firstUpdatedAt)
//                .build();
//
//        assertEquals(expected, actual);
//    }
//}
