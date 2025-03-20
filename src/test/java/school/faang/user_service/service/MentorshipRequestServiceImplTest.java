package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorship.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapperImpl;
import school.faang.user_service.mapper.RequestStatusMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship_request_filter_test.MentorshipRequestDescriptionFilterTest;
import school.faang.user_service.service.mentorship_request_filter_test.MentorshipRequestStatusFilterTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Mock
    private RequestStatusMapper requestStatusMapper;

    @InjectMocks
    private MentorshipRequestMapperImpl mentorshipRequestMapper;

    @Mock
    private UserRepository userRepository;

    private final List<MentorshipRequestFilter> mentorshipRequestFilters = List.of(
            new MentorshipRequestStatusFilterTest(),
            new MentorshipRequestDescriptionFilterTest()
    );

    private MentorshipRequestService service;

    private MentorshipRequestDto mentorshipRequestDto;

    @BeforeEach
    void setUp() {
        service = new MentorshipRequestServiceImpl(
                mentorshipRequestRepository,
                mentorshipRequestMapper,
                userRepository,
                mentorshipRequestFilters);

        ReflectionTestUtils.setField(service, "minRequestIntervalInMonths", 3);

        mentorshipRequestDto = new MentorshipRequestDto();
    }

    @Test
    @DisplayName("The exception is thrown away if the user does not exist")
    void testRequestMentorshipTheUserIsAbsent() {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(false);

        assertThrowsExactly(IllegalArgumentException.class, () -> service.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Throws an exception when there is not enough time between the requests")
    void testRequestMentorshipNotEnoughTimeBetweenRequests() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.of(mentorshipRequest));

        assertThrowsExactly(DataValidationException.class, () -> service.requestMentorship(mentorshipRequestDto));
        verify(mentorshipRequestRepository, times(1))
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
    }

    @Test
    @DisplayName("The method works correctly when enough time has passed between requests")
    void testRequestMentorshipEnoughTimeBetweenRequests() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.of(mentorshipRequest));

        assertDoesNotThrow(() -> service.requestMentorship(mentorshipRequestDto));

        verify(mentorshipRequestRepository, times(1))
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
    }

    @Test
    @DisplayName("The method works correctly when there is no previous request")
    void testRequestMentorshipThereIsNoPreviousRequest() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.empty());

        assertDoesNotThrow(() -> service.requestMentorship(mentorshipRequestDto));

        verify(mentorshipRequestRepository, times(1))
                .findLatestRequest(mentorshipRequestDto.getRequesterId(), mentorshipRequestDto.getReceiverId());
    }

    @Test
    @DisplayName("Checking for exclusion of exception with the same ID")
    void testRequestMentorshipExclusionWithTheSameIDUsers() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(1L);

        when(userRepository.existsById(mentorshipRequestDto.getRequesterId()))
                .thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId()))
                .thenReturn(true);

        assertThrows(DataValidationException.class, () -> service.requestMentorship(mentorshipRequestDto));
    }

    @Test
    @DisplayName("Test checks that a request is created")
    void testRequestMentorshipCreate() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        mentorshipRequestDto.setDescription("test");

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.empty());

        service.requestMentorship(mentorshipRequestDto);

        verify(mentorshipRequestRepository, times(1)).create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
    }

    @Test
    @DisplayName("Checking consistent filtering. One dto is returning")
    void testGetRequestsReturnOfOneDtoAfterFiltering() {
        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("test");
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("testDescription");
        mentorshipRequestSecond.setStatus(RequestStatus.REJECTED);

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setId(0L);
        mentorshipRequestDto.setDescription("test");
        mentorshipRequestDto.setStatus(requestStatusMapper.requestStatusToRequestStatusDto(RequestStatus.ACCEPTED));

        mockMentorshipRequestRepository(mentorshipRequestFirst, mentorshipRequestSecond);

        List<MentorshipRequestDto> mentorshipRequestsResult = service.getRequests(new RequestFilterDto());

        assertEquals(1, mentorshipRequestsResult.size());
        assertEquals(mentorshipRequestDto, mentorshipRequestsResult.get(0));
    }

    @Test
    @DisplayName("Checking consistent filtering. Two dto returns")
    void testGetRequestsReturnOfTwoDtoAfterFiltering() {
        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("test");
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("test");
        mentorshipRequestSecond.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequestDto mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setId(0L);
        mentorshipRequestDto.setDescription("test");
        mentorshipRequestDto.setStatus(requestStatusMapper.requestStatusToRequestStatusDto(RequestStatus.ACCEPTED));

        mockMentorshipRequestRepository(mentorshipRequestFirst, mentorshipRequestSecond);

        List<MentorshipRequestDto> mentorshipRequestsResult = service.getRequests(new RequestFilterDto());

        assertEquals(2, mentorshipRequestsResult.size());
        assertEquals(mentorshipRequestDto, mentorshipRequestsResult.get(0));
        assertEquals(mentorshipRequestDto, mentorshipRequestsResult.get(1));
    }

    @Test
    @DisplayName("Checking consistent filtering. An empty sheet returns")
    void testGetRequestsTheReturnOfTheEmptySheet() {
        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("testDescription");
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("test");
        mentorshipRequestSecond.setStatus(RequestStatus.PENDING);

        mockMentorshipRequestRepository(mentorshipRequestFirst, mentorshipRequestSecond);

        List<MentorshipRequestDto> mentorshipRequestsResult = service.getRequests(new RequestFilterDto());

        assertEquals(0, mentorshipRequestsResult.size());
    }

    @Test
    void testAcceptRequestCheckingTheRequest() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(new ArrayList<>());
        receiver.setMentees(new ArrayList<>());

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);

        when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        service.acceptRequest(0L);

        verify(mentorshipRequestRepository, times(1)).findById(0L);
    }

    @Test
    @DisplayName("Verification of exclusion is checked if Optional is empty")
    void testAcceptRequestOptionalIsEmpty() {
        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.empty();

        when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    @DisplayName("Exception throwing out if the requested already has a receiver in the list of mentors")
    void testAcceptRequestReceiverAlreadyOnTheListMentorsRequester() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(List.of(receiver));
        receiver.setMentees(List.of());

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);
        when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    @DisplayName("The exception will be thrown away if the receiver already has a requesting in the mentees list")
    void testAcceptRequestReceiverAlreadyOnTheListMenteesReceiver() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(List.of());
        receiver.setMentees(List.of(requester));

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);
        when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    @DisplayName("We check that the deviation was successful")
    void testRejectRequestSuccessfulDeviation() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("test");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);

        when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        service.rejectRequest(0L, rejectionDto);

        verify(mentorshipRequestRepository, times(1)).findById(0L);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("test", mentorshipRequest.getRejectionReason());
    }

    @Test
    @DisplayName("Exception is the exception if Optional is empty")
    void testRejectRequestOptionalIsEmpty() {
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason("test");

        when(mentorshipRequestRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> service.rejectRequest(0L, rejectionDto));
    }

    private void mockRepositoriesForExistingUsersWithPreviousRequest(Optional<MentorshipRequest> mentorshipRequest) {
        when(userRepository.existsById(mentorshipRequestDto.getRequesterId())).thenReturn(true);
        when(userRepository.existsById(mentorshipRequestDto.getReceiverId())).thenReturn(true);
        when(mentorshipRequestRepository.findLatestRequest(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId()
        )).thenReturn(mentorshipRequest);
    }

    private void mockMentorshipRequestRepository(
            MentorshipRequest mentorshipRequestFirst,
            MentorshipRequest mentorshipRequestSecond) {
        Iterable<MentorshipRequest> mentorshipRequests = ImmutableList.of(
                mentorshipRequestFirst,
                mentorshipRequestSecond);

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);
    }
}