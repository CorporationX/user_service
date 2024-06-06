//package school.faang.user_service.service.service.mentorship;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
//import school.faang.user_service.dto.mentorship.RequestFilterDto;
//import school.faang.user_service.entity.MentorshipRequest;
//import school.faang.user_service.entity.RequestStatus;
//import school.faang.user_service.entity.User;
//import school.faang.user_service.filter.mentorship.MentorshipRequestDescriptionFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestReceiverIdFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestRequesterIdFilter;
//import school.faang.user_service.filter.mentorship.MentorshipRequestStatusFilter;
//import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
//import school.faang.user_service.repository.UserRepository;
//import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
//import school.faang.user_service.service.mentorship.MentorshipRequestService;
//import school.faang.user_service.service.mentorship.filter.impl.MentorshipRequestFilterServiceImpl;
//import school.faang.user_service.service.mentorship.impl.MentorshipRequestServiceImpl;
//import school.faang.user_service.validator.mentorship.MentorshipRequestValidator;
//import school.faang.user_service.validator.user.UserValidator;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//public class MentorshipRequestServiceFilterTest {
//    private final MentorshipRequestDescriptionFilter descriptionFilter = new MentorshipRequestDescriptionFilter();
//    private final MentorshipRequestReceiverIdFilter receiverIdFilter = new MentorshipRequestReceiverIdFilter();
//    private final MentorshipRequestRequesterIdFilter requesterIdFilter = new MentorshipRequestRequesterIdFilter();
//    private final MentorshipRequestStatusFilter statusFilter = new MentorshipRequestStatusFilter();
//    private MentorshipRequestDto mrDtoWithId1;
//    private MentorshipRequestDto mrDtoWithId2;
//    private MentorshipRequestDto mrDtoWithId3;
//    private MentorshipRequestDto mrDtoWithId4;
//    private MentorshipRequestDto mrDtoWithId5;
//    private MentorshipRequest mrEntityWithId1;
//    private MentorshipRequest mrEntityWithId2;
//    private MentorshipRequest mrEntityWithId3;
//    private MentorshipRequest mrEntityWithId4;
//    private MentorshipRequest mrEntityWithId5;
//    private Long userId1;
//    private Long userId2;
//    private Long userId3;
//    private Long userId4;
//    private Long userId5;
//    private Long mentorshipRequestId1;
//    private Long mentorshipRequestId2;
//    private Long mentorshipRequestId3;
//    private Long mentorshipRequestId4;
//    private Long mentorshipRequestId5;
//    private User userWithId1;
//    private User userWithId2;
//    private User userWithId3;
//    private User userWithId4;
//    private User userWithId5;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private String descriptionOfMRWithId1;
//    private String descriptionOfMRWithId2;
//    private String descriptionOfMRWithId3;
//    private String descriptionOfMRWithId4;
//    private String descriptionOfMRWithId5;
//    private String rejectionReasonOfMRWithId1;
//    private String rejectionReasonOfMRWithId2;
//    private String rejectionReasonOfMRWithId3;
//    private String rejectionReasonOfMRWithId4;
//    private String rejectionReasonOfMRWithId5;
//    private List<MentorshipRequest> listOfEntities;
//    private RequestFilterDto filterDto;
//    @Mock
//    private MentorshipRequestRepository mentorshipRequestRepository;
//    @Mock
//    private MentorshipRequestValidator mentorshipRequestValidator;
//    @Mock
//    private UserValidator userValidator;
//    @Mock
//    private UserRepository userRepository;
//    @Spy
//    private MentorshipRequestMapperImpl mapper;
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
//        mentorshipRequestId5 = 5L;
//
//
//        createdAt = LocalDateTime.of(2022, 1, 1, 12, 0);
//        updatedAt = LocalDateTime.of(2022, 1, 1, 17, 0);
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
//        descriptionOfMRWithId5 = "description of a mentorship request with id 5";
//
//
//        rejectionReasonOfMRWithId1 = "rejection reason of a mentorship request with id 1";
//        rejectionReasonOfMRWithId2 = "rejection reason of a mentorship request with id 2";
//        rejectionReasonOfMRWithId3 = "rejection reason of a mentorship request with id 3";
//        rejectionReasonOfMRWithId4 = "rejection reason of a mentorship request with id 4";
//        rejectionReasonOfMRWithId5 = "rejection reason of a mentorship request with id 5";
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
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
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
//        mrDtoWithId5 = MentorshipRequestDto.builder()
//                .id(mentorshipRequestId5)
//                .description(descriptionOfMRWithId5)
//                .requesterId(userId3)
//                .receiverId(userId5)
//                .status(RequestStatus.ACCEPTED)
//                .rejectionReason(rejectionReasonOfMRWithId5)
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
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
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
//        mrEntityWithId5 = MentorshipRequest.builder()
//                .id(mentorshipRequestId5)
//                .description(descriptionOfMRWithId5)
//                .requester(userWithId3)
//                .receiver(userWithId5)
//                .status(RequestStatus.ACCEPTED)
//                .rejectionReason(rejectionReasonOfMRWithId5)
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//
//        listOfEntities = List.of(mrEntityWithId1, mrEntityWithId2, mrEntityWithId3, mrEntityWithId4, mrEntityWithId5);
//        filterDto = RequestFilterDto.builder()
//                .build();
//    }
//
//    private MentorshipRequestService setUp() {
//        var filters = new ArrayList<>(List.of(descriptionFilter, requesterIdFilter, receiverIdFilter, statusFilter));
//        when(mentorshipRequestRepository.findAll())
//                .thenReturn(listOfEntities);
//        var mentorshipRequestFilterService = new MentorshipRequestFilterServiceImpl(filters);
//
//        return new MentorshipRequestServiceImpl(
//                mentorshipRequestRepository,
//                mentorshipRequestFilterService,
//                mentorshipRequestValidator,
//                mapper,
//                userRepository,
//                userValidator
//        );
//    }
//
//    @Test
//    public void testGetRequestsFiltersEntitiesByRequesterIdAndReceiverId() {
//        when(mentorshipRequestRepository.findAll())
//                .thenReturn(listOfEntities);
//        filterDto.setRequesterIdPattern(userId3);
//        filterDto.setReceiverIdPattern(userId5);
//        var mentorshipRequestService = setUp();
//        var actual = mentorshipRequestService.getRequests(filterDto);
//        var expected = List.of(mrDtoWithId1, mrDtoWithId5);
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testGetRequestsFiltersEntitiesByRequesterIdAndDescription() {
//        filterDto.setRequesterIdPattern(userId3);
//        filterDto.setDescriptionPattern("a mentorship request with id 5");
//        var mentorshipRequestService = setUp();
//        var actual = mentorshipRequestService.getRequests(filterDto);
//        var expected = List.of(mrDtoWithId5);
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testGetRequestsFiltersEntitiesByRequesterIdAndStatusPending() {
//        filterDto.setRequesterIdPattern(userId3);
//        filterDto.setStatusPattern(RequestStatus.PENDING);
//        var mentorshipRequestService = setUp();
//        var actual = mentorshipRequestService.getRequests(filterDto);
//        var expected = List.of(mrDtoWithId1, mrDtoWithId3);
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testGetRequestsFiltersEntitiesByRequesterIdAndStatusAccepted() {
//        filterDto.setRequesterIdPattern(userId3);
//        filterDto.setStatusPattern(RequestStatus.ACCEPTED);
//        var mentorshipRequestService = setUp();
//        var actual = mentorshipRequestService.getRequests(filterDto);
//        var expected = List.of(mrDtoWithId5);
//
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void testGetRequestsFiltersEntitiesShouldReturnEmptyList() {
//        filterDto.setRequesterIdPattern(userId4);
//        filterDto.setReceiverIdPattern(userId3);
//        var mentorshipRequestService = setUp();
//        var actual = mentorshipRequestService.getRequests(filterDto);
//        var expected = Collections.emptyList();
//
//        assertEquals(expected, actual);
//    }
//}
