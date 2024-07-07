package school.faang.user_service.service;


import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestDescrFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestReceiverFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestRequesterFilter;
import school.faang.user_service.service.mentorship_request_filter.MentorshipRequestStatusFilter;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {

    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;

    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRepository mentorshipRepository;
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Spy
    private List<MentorshipRequestFilter> requestFilters = new ArrayList<>(
            List.of(
                    new MentorshipRequestDescrFilter(),
                    new MentorshipRequestReceiverFilter(),
                    new MentorshipRequestRequesterFilter(),
                    new MentorshipRequestStatusFilter()
            )
    );
    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;
    @Captor
    private ArgumentCaptor<User> userCaptor;
    private MentorshipRequestDto dto;
    private MentorshipRequestDto requestDto;
    private MentorshipRequest entity;

    @BeforeEach
    public void initializeData() {
        this.dto = new MentorshipRequestDto(
                1L, "desc", 1L, 2L, RequestStatus.PENDING, "reason",
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40),
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)
        );
        this.requestDto = new MentorshipRequestDto(
                1L, "desc", 1L, 2L, RequestStatus.PENDING, "reason",
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40),
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)
        );
        this.entity = new MentorshipRequest(
                1L, "desc", User.builder().id(1L).build(), User.builder().id(2L).build(),
                RequestStatus.PENDING, "reason",LocalDateTime.of(2024,
                Month.AUGUST, 8, 19, 30, 40),
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)
        );
    }

    @Test
    public void testDtoIsEmpty() {
        dto = null;
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals("Дто не может быть пустым", exception.getMessage());
    }

    @Test
    public void testRequesterIsEmpty() {
        dto.setRequesterId(null);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals("Пользователь, который отправляет запрос на менторство не может быть" +
                 " быть пустым", exception.getMessage());

    }

    @Test
    public void testReceiverIsEmpty() {
        dto.setReceiverId(null);
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals("Пользователь, которому направляется запрос на менторство не может" +
                "быть пустым", exception.getMessage());
    }

    @Test
    public void testRequesterEqualsReceiver() {
        dto.setRequesterId(dto.getReceiverId());
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals("Вы сделали запрос на менторство самому себе", exception.getMessage());
    }

    @Test
    public void testRequesterNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals("Пользователя, который запрашивает менторство, нет в бд", exception.getMessage());
    }

    @Test
    public void testReceiverNotFound() {
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        lenient().when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals(
                "Пользователя, которому направляют запрос на менторство, нет в бд",
                exception.getMessage()
        );
    }

    private void prepareMockUserRepository() {
        lenient().when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
    }

    @Test
    public void testRequestThreeMonth() {
        MentorshipRequest returnEntity = new MentorshipRequest();
        prepareMockUserRepository();
        returnEntity.setCreatedAt(dto.getCreatedAt().minusMonths(2));
        when(mentorshipRequestRepository.findLatestRequest(dto.getRequesterId(), dto.getReceiverId()))
                .thenReturn(Optional.of(returnEntity));
        Exception exception = Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.requestMentorship(dto);
        });
        Assertions.assertEquals(
                "Запрос на менторство можно отправить только раз в 3 месяца",
                exception.getMessage()
        );
    }

    @Test
    public void testRequestRepositorySave() {
        prepareMockUserRepository();
        when(mentorshipRequestRepository.save(entity)).thenReturn(entity);
        MentorshipRequestDto returnDto = mentorshipRequestService.requestMentorship(dto);
        verify(mentorshipRequestRepository, times(1)).save(requestCaptor.capture());
        MentorshipRequest requestEntity = requestCaptor.getValue();
        Assertions.assertEquals(entity, requestEntity);
        Assertions.assertEquals(requestDto, returnDto);

    }

    @Test
    public void testGetRequestsFilter() {
        List<MentorshipRequest> mentorshipRequests = new ArrayList<>(List.of(
                new MentorshipRequest(1L, "desc123", User.builder().id(1L).build(), User.builder().id(2L).build(),
                        RequestStatus.ACCEPTED, "reason",LocalDateTime.of(2024,
                        Month.AUGUST, 8, 19, 30, 40),
                        LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)),
                new MentorshipRequest(2L, "desc", User.builder().id(1L).build(), User.builder().id(2L).build(),
                        RequestStatus.ACCEPTED, "reason",LocalDateTime.of(2024,
                        Month.AUGUST, 8, 19, 30, 40),
                        LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 42)),
                new MentorshipRequest(3L, "desc123", User.builder().id(3L).build(), User.builder().id(2L).build(),
                        RequestStatus.ACCEPTED, "reason",LocalDateTime.of(2024,
                        Month.AUGUST, 8, 19, 30, 40),
                        LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 44)),
                new MentorshipRequest(4L, "desc123", User.builder().id(1L).build(), User.builder().id(4L).build(),
                        RequestStatus.ACCEPTED, "reason",LocalDateTime.of(2024,
                        Month.AUGUST, 8, 19, 30, 40),
                        LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 46)),
                new MentorshipRequest(5L, "desc123", User.builder().id(1L).build(), User.builder().id(2L).build(),
                        RequestStatus.PENDING, "reason",LocalDateTime.of(2024,
                        Month.AUGUST, 8, 19, 30, 40),
                        LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40))
        ));

        when(mentorshipRequestRepository.findAll()).thenReturn(mentorshipRequests);
        RequestFilterDto requestFilter = new RequestFilterDto("123", 1L,2L, RequestStatus.ACCEPTED);
        List<MentorshipRequestDto> requestDtos = mentorshipRequestService.getRequests(requestFilter);
        List<MentorshipRequestDto> expectedDtos = mentorshipRequestMapper.toDto(new ArrayList<>(List.of(mentorshipRequests.get(0))));
        Assertions.assertEquals(expectedDtos, requestDtos);
    }

    @Test
    public void testAcceptRequestNull() {
        long id = 1L;
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(id));
        Assertions.assertEquals("Запроса на менторство нет в БД", exception.getMessage());
    }

    @Test
    public void testAcceptRequestUserAlreadyHaveThisMentor() {
        long id = 1L;
        long userId = 1L;
        MentorshipRequest mentorshipRequest =  new MentorshipRequest();
        mentorshipRequest.setRequester(User.builder().id(userId).build());
        mentorshipRequest.setReceiver(User.builder().id(userId).build());
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.findRequests(userId, userId)).thenReturn(Optional.of( new MentorshipRequest()));

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,() -> {
            mentorshipRequestService.acceptRequest(id);
        });
        Assertions.assertEquals("Пользователь уже является ментором для данного пользователя", exception.getMessage());
    }

    @Test
    public void testAcceptRequestRequesterHaveNotInDb() {
        long id = 1L;
        long userId = 1L;
        MentorshipRequest mentorshipRequest =  new MentorshipRequest();
        mentorshipRequest.setRequester(User.builder().id(userId).build());
        mentorshipRequest.setReceiver(User.builder().id(userId).build());
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.findRequests(userId, userId)).thenReturn(Optional.empty());

        when(mentorshipRepository.findById(userId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(id));
        Assertions.assertEquals("Пользователя нет в таблице пользователей", exception.getMessage());
    }

    @Test
    public void testAcceptRequestMentorHaveNotInDb() {
        long id = 1L;
        long userId = 1L;
        long receiverId = 2L;
        MentorshipRequest mentorshipRequest =  new MentorshipRequest();
        mentorshipRequest.setRequester(User.builder().id(userId).build());
        mentorshipRequest.setReceiver(User.builder().id(receiverId).build());
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.findRequests(userId, receiverId)).thenReturn(Optional.empty());

        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(new User()));
        lenient().when(mentorshipRepository.findById(receiverId)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> mentorshipRequestService.acceptRequest(id));
        Assertions.assertEquals("Ментора нет в таблице пользователей", exception.getMessage());

    }
    @Test
    public void testAcceptRequestSaveToDb() {
        long id = 1L;
        long userId = 1L;
        long receiverId = 2L;
        MentorshipRequest mentorshipRequest =  new MentorshipRequest();
        mentorshipRequest.setRequester(User.builder().id(userId).build());
        mentorshipRequest.setReceiver(User.builder().id(receiverId).build());
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        lenient().when(mentorshipRequestRepository.findRequests(userId, receiverId)).thenReturn(Optional.empty());

        User user = User.builder()
                .id(10L)
                .mentors(new ArrayList<>(List.of(User.builder().id(3L).build())))
                .build();
        User saveUser = User.builder()
                .id(10L)
                .mentors(new ArrayList<>(List.of(
                        User.builder().id(3L).build(),
                        User.builder().id(receiverId).build()
                )))
                .build();
        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(mentorshipRepository.findById(receiverId)).thenReturn(Optional.of(User.builder().id(receiverId).build()));
        mentorshipRequestService.acceptRequest(id);
        verify(mentorshipRepository, times(1)).save(userCaptor.capture());
        Assertions.assertEquals(saveUser, userCaptor.getValue());

    }


    @Test
    public void testAcceptRequestReturn() {
        long id = 1L;
        long userId = 1L;
        long receiverId = 2L;
        MentorshipRequest mentorshipRequest =  new MentorshipRequest();
        mentorshipRequest.setId(100L);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setRequester(User.builder().id(userId).build());
        mentorshipRequest.setReceiver(User.builder().id(receiverId).build());
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        lenient().when(mentorshipRequestRepository.findRequests(userId, receiverId)).thenReturn(Optional.empty());

        User user = User.builder()
                .id(10L)
                .mentors(new ArrayList<>(List.of(User.builder().id(3L).build())))
                .build();

        when(mentorshipRepository.findById(userId)).thenReturn(Optional.of(user));
        lenient().when(mentorshipRepository.findById(receiverId)).thenReturn(Optional.of(User.builder().id(receiverId).build()));
        MentorshipRequestDto dto = mentorshipRequestService.acceptRequest(id);

        MentorshipRequest mentorshipRequestEtalon =  new MentorshipRequest();
        mentorshipRequestEtalon.setId(100L);
        mentorshipRequestEtalon.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestEtalon.setRequester(User.builder().id(userId).build());
        mentorshipRequestEtalon.setReceiver(User.builder().id(receiverId).build());
        MentorshipRequestDto dtoEtalon = mentorshipRequestMapper.toDto(mentorshipRequestEtalon);
        Assertions.assertEquals(dtoEtalon, dto);
    }

    @Test
    public void testRejectRequestRejectionNull() {
        long id = 1;
        RejectionDto rejection = null;
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.rejectRequest(id, rejection);
        });
        Assertions.assertEquals("Пустая причина отказа", exception.getMessage());
    }

    @Test
    public void testRejectRequestReasonNull() {
        long id = 1;
        RejectionDto rejection = new RejectionDto(1L, "  ");
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.rejectRequest(id, rejection);
        });
        Assertions.assertEquals("Пустая причина отказа", exception.getMessage());
    }

    @Test
    public void testRejectRequestNull() {
        long id = 1;
        RejectionDto rejection = new RejectionDto(2L, "asd");
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipRequestService.rejectRequest(id, rejection);
        });
        Assertions.assertEquals("Переданного запроса нет в базе", exception.getMessage());
    }

    @Test
    public void testRejectRequestSaveToDb() {
        long id = 1;
        RejectionDto rejection = new RejectionDto(2L, "Занят");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequestEtalon = new MentorshipRequest();
        mentorshipRequestEtalon.setId(1L);
        mentorshipRequestEtalon.setStatus(RequestStatus.REJECTED);
        mentorshipRequestEtalon.setRejectionReason("Занят");
        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));

        mentorshipRequestService.rejectRequest(id, rejection);
        verify(mentorshipRequestRepository, times(1)).save(requestCaptor.capture());
        Assertions.assertEquals(mentorshipRequestEtalon, requestCaptor.getValue());
    }

    @Test
    public void testRejectRequestReturn() {
        long id = 1;
        RejectionDto rejection = new RejectionDto(2L, "Занят");
        MentorshipRequest mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setId(1L);
        mentorshipRequest.setStatus(RequestStatus.ACCEPTED);
        MentorshipRequest mentorshipRequestEtalon = new MentorshipRequest();
        mentorshipRequestEtalon.setId(1L);
        mentorshipRequestEtalon.setStatus(RequestStatus.REJECTED);
        mentorshipRequestEtalon.setRejectionReason("Занят");
        MentorshipRequestDto mentorshipRequestDtoEtalon = mentorshipRequestMapper.toDto(mentorshipRequestEtalon);

        when(mentorshipRequestRepository.findById(id)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestRepository.save(mentorshipRequestEtalon)).thenReturn(mentorshipRequestEtalon);
        MentorshipRequestDto dto = mentorshipRequestService.rejectRequest(id, rejection);
        Assertions.assertEquals(mentorshipRequestDtoEtalon, dto);
    }
}