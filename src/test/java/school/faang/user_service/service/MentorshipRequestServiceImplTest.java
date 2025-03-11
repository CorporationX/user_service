package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
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
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceImplTest {

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;

    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Mock
    private UserRepository userRepository;

    private final List<MentorshipRequestFilter> mentorshipRequestFilters = List.of(
            new TestMentorshipMentorshipRequestStatusFilter(),
            new TestMentorshipMentorshipRequestDescriptionFilter()
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
    void testEnsureUserExistsById() {
        Mockito.when(userRepository.existsById(mentorshipRequestDto.getRequesterId()))
                .thenReturn(false);

        assertThrowsExactly(IllegalArgumentException.class,
                () -> service.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void testRequestMentorshipThrowsExceptionWhenLastRequestIsTooRecent() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(1));

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.of(mentorshipRequest));

        assertThrowsExactly(DataValidationException.class,
                () -> service.requestMentorship(mentorshipRequestDto));
        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .findLatestRequest(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
    }

    @Test
    void testRequestMentorshipAllowsRequestWhenLastRequestIsOldEnough() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setCreatedAt(LocalDateTime.now().minusMonths(4));

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.of(mentorshipRequest));

        assertDoesNotThrow(() -> service.requestMentorship(mentorshipRequestDto));

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .findLatestRequest(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
    }

    @Test
    void requestMentorshipWhenNoPreviousRequestExistsAllowsRequest() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.empty());

        assertDoesNotThrow(() -> service.requestMentorship(mentorshipRequestDto));

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .findLatestRequest(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId());
    }

    @Test
    void ensureRequesterIsNotReceiver() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(1L);

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.empty());

        assertThrows(DataValidationException.class, () -> service.requestMentorship(mentorshipRequestDto));
    }

    @Test
    void testCreateRequestMentorship() {
        mentorshipRequestDto.setRequesterId(1L);
        mentorshipRequestDto.setReceiverId(6L);
        mentorshipRequestDto.setDescription("test");

        mockRepositoriesForExistingUsersWithPreviousRequest(Optional.empty());

        service.requestMentorship(mentorshipRequestDto);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .create(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId(),
                        mentorshipRequestDto.getDescription());
    }

    @Test
    void testGetRequestsReturnsSingleAcceptedRequestWhenOneAcceptedAndOneRejectedRequestExist() {
        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("test");
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("testDescription");
        mentorshipRequestSecond.setStatus(RequestStatus.REJECTED);

        MentorshipRequestDto mentorshipRequestDtoFirst = new MentorshipRequestDto();
        mentorshipRequestDtoFirst.setId(0L);
        mentorshipRequestDtoFirst.setDescription("test");
        mentorshipRequestDtoFirst.setStatus(RequestStatus.ACCEPTED);

        mockMentorshipRequestRepository(mentorshipRequestFirst, mentorshipRequestSecond);

        List<MentorshipRequestDto> mentorshipRequestsResult = service.getRequests(new RequestFilterDto());

        assertEquals(1, mentorshipRequestsResult.size());
        assertEquals(mentorshipRequestDtoFirst, mentorshipRequestsResult.get(0));
    }

    @Test
    void testGetRequestsReturnsAllAcceptedRequestsWhenMultipleAcceptedRequestsExist() {
        MentorshipRequest mentorshipRequestFirst = new MentorshipRequest();
        mentorshipRequestFirst.setDescription("test");
        mentorshipRequestFirst.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequest mentorshipRequestSecond = new MentorshipRequest();
        mentorshipRequestSecond.setDescription("test");
        mentorshipRequestSecond.setStatus(RequestStatus.ACCEPTED);

        MentorshipRequestDto mentorshipRequestDtoFirst = new MentorshipRequestDto();
        mentorshipRequestDtoFirst.setId(0L);
        mentorshipRequestDtoFirst.setDescription("test");
        mentorshipRequestDtoFirst.setStatus(RequestStatus.ACCEPTED);

        mockMentorshipRequestRepository(mentorshipRequestFirst, mentorshipRequestSecond);

        List<MentorshipRequestDto> mentorshipRequestsResult = service.getRequests(new RequestFilterDto());

        assertEquals(2, mentorshipRequestsResult.size());
        assertEquals(mentorshipRequestDtoFirst, mentorshipRequestsResult.get(0));
        assertEquals(mentorshipRequestDtoFirst, mentorshipRequestsResult.get(1));
    }

    @Test
    void testGetRequestsReturnsEmptyListWhenNoRequestsMatchFilterCriteria() {
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
    void testAcceptRequest() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(new ArrayList<>());
        receiver.setMentees(new ArrayList<>());

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);

        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        service.acceptRequest(0L);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1))
                .findById(0L);
    }

    @Test
    void testAcceptRequestOptionalIsNull() {
        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.empty();

        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    void testAcceptRequestReceiverAlreadyOnTheListMentorsRequester() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(List.of(receiver));
        receiver.setMentees(List.of());

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);
        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    void testAcceptRequestReceiverAlreadyOnTheListMenteesReceiver() {
        User requester = new User();
        User receiver = new User();
        requester.setMentors(List.of());
        receiver.setMentees(List.of(requester));

        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);

        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);
        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        assertThrows(DataValidationException.class, () -> service.acceptRequest(0L));
    }

    @Test
    void testRejectRequest() {
        RejectionDto rejectionDto = new RejectionDto("test");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        Optional<MentorshipRequest> mentorshipRequestOptional = Optional.of(mentorshipRequest);

        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(mentorshipRequestOptional);

        service.rejectRequest(0L, rejectionDto);

        Mockito.verify(mentorshipRequestRepository, Mockito.times(1)).findById(0L);
        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("test", mentorshipRequest.getRejectionReason());
    }

    @Test
    void testRejectRequestOptionalIsNull() {
        RejectionDto rejectionDto = new RejectionDto("test");

        Mockito.when(mentorshipRequestRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(DataValidationException.class, () -> service.rejectRequest(0L, rejectionDto));
    }

    private void mockRepositoriesForExistingUsersWithPreviousRequest(Optional<MentorshipRequest> mentorshipRequest) {
        Mockito.when(userRepository.existsById(mentorshipRequestDto.getRequesterId()))
                .thenReturn(true);
        Mockito.when(userRepository.existsById(mentorshipRequestDto.getReceiverId()))
                .thenReturn(true);
        Mockito.when(mentorshipRequestRepository.findLatestRequest(
                        mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId()))
                .thenReturn(mentorshipRequest);
    }

    private void mockMentorshipRequestRepository(
            MentorshipRequest mentorshipRequestFirst, MentorshipRequest mentorshipRequestSecond) {
        Iterable<MentorshipRequest> mentorshipRequests = ImmutableList.of(
                mentorshipRequestFirst,
                mentorshipRequestSecond);

        Mockito.when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);
    }
}