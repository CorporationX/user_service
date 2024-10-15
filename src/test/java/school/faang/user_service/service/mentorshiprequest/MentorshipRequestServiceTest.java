package school.faang.user_service.service.mentorshiprequest;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorshiprequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshiprequest.RejectionDto;
import school.faang.user_service.dto.mentorshiprequest.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.mentorshiprequest.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshiprequest.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipRequestEventPublisher;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.mentorshiprequst.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService menReqService;
    @Mock
    private MentorshipRequestMapper menReqMapper;
    @Mock
    private MentorshipRequestValidator menReqValidator;
    @Mock
    private MentorshipRequestRepository menReqRepository;
    @Mock
    private MentorshipRequestEventPublisher publisher;

    private MentorshipRequestDto menReqDto = new MentorshipRequestDto();
    private MentorshipRequest menReqEntity = new MentorshipRequest();
    private RejectionDto rejectionDto = new RejectionDto();
    private User requester = new User();
    private User receiver = new User();
    private List<MentorshipRequestFilter> filterRequests;
    private static final long REQUESTER_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long MENTORSHIP_REQUEST_ID = 1L;
    private static final String DESCRIPTION_TEXT = "Test";
    private static final int MEN_REQS_SIZE = 2;

    @Nested
    class PositiveTest {
        @BeforeEach
        public void init() {
            List<User> mentors = new ArrayList<>();

            MentorshipRequestFilter requestFilter = mock(MentorshipRequestFilter.class);
            filterRequests = List.of(requestFilter);

            menReqService = new MentorshipRequestService(
                    menReqRepository, menReqMapper, menReqValidator, filterRequests, publisher);

            menReqDto = MentorshipRequestDto.builder()
                    .id(MENTORSHIP_REQUEST_ID)
                    .requesterId(REQUESTER_ID)
                    .receiverId(RECEIVER_ID)
                    .description(DESCRIPTION_TEXT)
                    .build();

            requester = User.builder()
                    .id(REQUESTER_ID)
                    .build();

            receiver = User.builder()
                    .id(RECEIVER_ID)
                    .mentors(mentors)
                    .build();

            menReqEntity = MentorshipRequest.builder()
                    .requester(requester)
                    .receiver(receiver)
                    .id(MENTORSHIP_REQUEST_ID)
                    .description(DESCRIPTION_TEXT)
                    .build();
        }

        @Test
        @DisplayName("Успешное создание запроса если запрос не был создан ранее")
        void whenCreateThenSaveRequest() {
            when(menReqRepository.findLatestRequest(menReqDto.getRequesterId(), menReqDto.getReceiverId()))
                    .thenReturn(Optional.of(menReqEntity));
            when(menReqRepository.create(menReqDto.getRequesterId(),
                    menReqDto.getReceiverId(), menReqDto.getDescription()))
                    .thenReturn(menReqEntity);
            when(menReqMapper.toDto(menReqEntity)).thenReturn(menReqDto);

            MentorshipRequestDto mentorshipRequestDto = menReqService.requestMentorship(menReqDto);

            assertNotNull(menReqEntity);
            assertNotNull(mentorshipRequestDto);
            assertEquals(menReqEntity.getRequester(), requester);
            assertEquals(menReqEntity.getReceiver(), receiver);
            assertEquals(menReqEntity.getDescription(), DESCRIPTION_TEXT);
            assertEquals(mentorshipRequestDto.getRequesterId(), REQUESTER_ID);
            assertEquals(mentorshipRequestDto.getReceiverId(), RECEIVER_ID);
            assertEquals(mentorshipRequestDto.getDescription(), DESCRIPTION_TEXT);


            verify(menReqValidator).validateReceiverNoEqualsRequester(menReqDto);
            verify(menReqValidator).validateAvailabilityUsersDB(menReqDto);
            verify(menReqValidator).validateDataCreateRequest(menReqEntity);
            verify(menReqRepository).create(menReqDto.getRequesterId(),
                    menReqDto.getReceiverId(), menReqDto.getDescription());
            verify(menReqRepository).findLatestRequest(menReqDto.getRequesterId(), menReqDto.getReceiverId());
            verify(menReqMapper).toDto(menReqEntity);
        }


        @Test
        @DisplayName("Успешное получение запроса")
        void whenGetRequestsByFilterThenSuccess() {
            RequestFilterDto requestFilterDto = new RequestFilterDto();
            requestFilterDto.setStatus(RequestStatus.ACCEPTED);

            MentorshipRequest firstRequest = new MentorshipRequest();
            firstRequest.setStatus(RequestStatus.ACCEPTED);
            MentorshipRequest secondRequest = new MentorshipRequest();
            secondRequest.setStatus(RequestStatus.ACCEPTED);

            menReqDto.setStatus(RequestStatus.ACCEPTED);

            when(menReqRepository.findAll()).thenReturn(List.of(firstRequest, secondRequest));
            when(filterRequests.get(0).isApplicable(requestFilterDto)).thenReturn(true);
            when(filterRequests.get(0).apply(any(), eq(requestFilterDto))).thenReturn(Stream.of(firstRequest, secondRequest));
            when(menReqMapper.toDto(firstRequest)).thenReturn(menReqDto);

            List<MentorshipRequestDto> result = menReqService.getRequests(requestFilterDto);

            assertNotNull(result);
            assertTrue(result.contains(menReqDto));
            assertEquals(MEN_REQS_SIZE, result.size());

            verify(menReqRepository).findAll();
            verify(filterRequests.get(0)).isApplicable(requestFilterDto);
            verify(filterRequests.get(0)).apply(any(), eq(requestFilterDto));
            verify(menReqMapper, times(2)).toDto(firstRequest);
        }

        @Test
        @DisplayName("Успешное принятие запроса")
        void whenAcceptThenSaveRequest() {
            menReqDto.setStatus(RequestStatus.ACCEPTED);
            menReqDto.setReceiverId(receiver.getId());

            when(menReqRepository.findById(MENTORSHIP_REQUEST_ID)).thenReturn(Optional.of(menReqEntity));
            when(menReqRepository.save(menReqEntity)).thenReturn(menReqEntity);
            when(menReqMapper.toDto(menReqEntity)).thenReturn(menReqDto);

            menReqEntity.getReceiver().getMentors().add(menReqEntity.getRequester());
            menReqEntity.setStatus(RequestStatus.ACCEPTED);

            MentorshipRequestDto mentorshipRequestDtoNew = menReqService.acceptRequest(MENTORSHIP_REQUEST_ID);

            assertNotNull(menReqEntity);
            assertNotNull(mentorshipRequestDtoNew);
            assertTrue(receiver.getMentors().contains(requester));
            assertEquals(menReqEntity.getStatus(), RequestStatus.ACCEPTED);
            assertEquals(menReqEntity.getRequester(), requester);
            assertEquals(menReqEntity.getReceiver(), receiver);
            assertEquals(mentorshipRequestDtoNew.getStatus(), menReqEntity.getStatus());
            assertEquals(mentorshipRequestDtoNew.getReceiverId(), menReqEntity.getReceiver().getId());
            assertEquals(mentorshipRequestDtoNew.getRequesterId(), menReqEntity.getRequester().getId());

            verify(menReqValidator).validateMentorsContainsReceiver(menReqEntity);
            verify(menReqRepository).findById(MENTORSHIP_REQUEST_ID);
            verify(menReqMapper).toDto(menReqEntity);
            verify(menReqRepository).save(menReqEntity);
        }

        @Test
        @DisplayName("Успешное отклонение запроса")
        void whenRejectThenSaveRequest() {
            menReqDto.setStatus(RequestStatus.REJECTED);

            when(menReqRepository.findById(MENTORSHIP_REQUEST_ID)).thenReturn(Optional.of(menReqEntity));
            when(menReqRepository.save(menReqEntity)).thenReturn(menReqEntity);
            when(menReqMapper.toDto(menReqEntity)).thenReturn(menReqDto);

            menReqEntity.setStatus(RequestStatus.REJECTED);
            menReqEntity.setRejectionReason(rejectionDto.getReason());

            MentorshipRequestDto menReqDtoNew = menReqService.rejectRequest(MENTORSHIP_REQUEST_ID, rejectionDto);

            assertNotNull(menReqEntity);
            assertNotNull(menReqDtoNew);
            assertEquals(menReqEntity.getStatus(), RequestStatus.REJECTED);
            assertEquals(menReqEntity.getRejectionReason(), menReqDtoNew.getRejectionReason());
            assertEquals(menReqDtoNew.getStatus(), menReqEntity.getStatus());
            assertEquals(menReqDtoNew.getRejectionReason(), menReqEntity.getRejectionReason());

            verify(menReqRepository).findById(MENTORSHIP_REQUEST_ID);
            verify(menReqRepository).save(menReqEntity);
            verify(menReqMapper).toDto(menReqEntity);
        }
    }

    @Nested
    class NegativeTest {
        @Test
        @DisplayName("Бросить исключение если пользователь отсутствует в БД")
        void whenValidateAcceptThenException() {
            when(menReqRepository.findById(MENTORSHIP_REQUEST_ID)).thenReturn(Optional.empty());

            assertEquals("No such request was found " + MENTORSHIP_REQUEST_ID,
                    assertThrows(EntityNotFoundException.class, () -> {
                        menReqService.acceptRequest(MENTORSHIP_REQUEST_ID);
                    }).getMessage());

            verify(menReqRepository).findById(MENTORSHIP_REQUEST_ID);
        }

        @Test
        @DisplayName("Бросить исключение если пользователь отсутствует в БД")
        void whenValidateRejectThenException() {
            when(menReqRepository.findById(MENTORSHIP_REQUEST_ID)).thenReturn(Optional.empty());

            assertEquals("No such request was found " + MENTORSHIP_REQUEST_ID,
                    assertThrows(EntityNotFoundException.class, () -> {
                        menReqService.rejectRequest(MENTORSHIP_REQUEST_ID, rejectionDto);
                    }).getMessage());

            verify(menReqRepository).findById(MENTORSHIP_REQUEST_ID);
        }
    }
}
