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
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.Mockito.calls;
import static org.mockito.Mockito.doReturn;
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
    @Spy
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);

    @Captor
    private ArgumentCaptor<MentorshipRequest> requestCaptor;
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

}
