package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.mentorship.MentorshipRequestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestControllerTest {

    private MentorshipRequestDto dto;
    private MentorshipRequestDto returnDto;
    @InjectMocks
    private MentorshipRequestController mentorshipRequestController;
    @Mock
    private MentorshipRequestService service;

    @Captor
    ArgumentCaptor<MentorshipRequestDto> captor;

    @Captor
    ArgumentCaptor<RequestFilterDto> captorFilter;

    @BeforeEach
    public void initializeDto() {
        this.dto = new MentorshipRequestDto(
                1L, null, 1L, 2L, RequestStatus.PENDING, "reason",
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40),
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)
        );
        this.returnDto = new MentorshipRequestDto(
                1L, "desc", 1L, 2L, RequestStatus.PENDING, "reason",
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40),
                LocalDateTime.of(2024, Month.AUGUST, 8, 19, 30, 40)
        );
    }

    // @ParameterizedTest
    // @ValueSource(strings={"   "})
    // @NullSource
    //public void testDescriptionIsNull(String descr) {
    @Disabled // Не проверяются поля DTO помеченные аннотациями из org.springframework.boot:spring-boot-starter-validation
    @Test
    public void testDescriptionIsNull() {
        dto.setDescription(null);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> mentorshipRequestController.requestMentorship(dto));

    }

    @Test
    public void testServiceRequestMentorship() {
        when(service.requestMentorship(dto)).thenReturn(returnDto);
        MentorshipRequestDto returnDtoFromMethod = mentorshipRequestController.requestMentorship(dto);
        verify(service, times(1)).requestMentorship(captor.capture());

        Assertions.assertEquals(returnDto, captor.getValue());
        Assertions.assertEquals(returnDto, returnDtoFromMethod);
    }

    @Test
    public void testGetRequests() {
        RequestFilterDto filter = RequestFilterDto.builder()
                .descriptionPattern("123")
                .requesterId(1L)
                .receiverId(1L)
                .status(String.valueOf(RequestStatus.PENDING))
                .build();
        RequestFilterDto returnFilter = RequestFilterDto.builder()
                .descriptionPattern("123")
                .requesterId(1L)
                .receiverId(1L)
                .status(RequestStatus.PENDING.toString())
                .build();
        mentorshipRequestController.getRequests(filter);
        verify(service, times(1)).getRequests(captorFilter.capture());
        Assertions.assertEquals(returnFilter, captorFilter.getValue());
    }
}
