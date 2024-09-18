package school.faang.user_service.controller.recommendation;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationRequestService;
import school.faang.user_service.service.filter.recommendation.RequestFilterDto;

import java.time.LocalDateTime;

import java.util.List;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestControllerTest {
    private RecommendationRequestDto dto;
    private RecommendationRequestDto returnDto;
    private RecommendationRequestDto expectedDto;
    private long id;
    private Long expectedId;
    @InjectMocks
    private RecommendationRequestController controller;
    @Mock
    private RecommendationRequestService service;
    @Captor
    private ArgumentCaptor<RecommendationRequestDto> captorRequestDto;
    @Captor
    private ArgumentCaptor<RequestFilterDto> captorFilter;
    @Captor
    private ArgumentCaptor<Long> captorId;
    @Captor
    private ArgumentCaptor<RejectionDto> captorRejection;

    @BeforeEach
    public void setup() {
        dto = new RecommendationRequestDto(
                1L,
                "message",
                null,
                null,
                2L,
                3L,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        returnDto = new RecommendationRequestDto(
                1L,
                "message",
                null,
                null,
                2L,
                3L,
                dto.createdAt(),
                dto.updatedAt()
        );
        expectedDto = new RecommendationRequestDto(
                1L,
                "message",
                null,
                null,
                2L,
                3L,
                dto.createdAt(),
                dto.updatedAt()
        );

        id = 1;
        expectedId = 1L;
    }

    @Test
    public void testRequestRecommendation_messageNull() {
        // Arrange
        dto = new RecommendationRequestDto(
                1L,
                null,
                null,
                null,
                2L,
                3L,
                dto.createdAt(),
                dto.updatedAt()
        );

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> controller.requestRecommendation(dto));
        Assertions.assertEquals("Сообщение не может быть пустым", exception.getMessage());
    }

    @Test
    public void testRequestRecommendation() {
        // Arrange
        when(service.create(dto)).thenReturn(returnDto);

        // Act and Assert
        RecommendationRequestDto receivedDto = controller.requestRecommendation(dto);
        verify(service, times(1)).create(captorRequestDto.capture());
        Assertions.assertEquals(expectedDto, captorRequestDto.getValue());
        Assertions.assertEquals(expectedDto, receivedDto);
    }

    @Test
    public void testGetRecommendationRequests() {
        // Arrange
        RequestFilterDto filter = new RequestFilterDto(RequestStatus.REJECTED, "pattern");
        RequestFilterDto expectedFilter = new RequestFilterDto(RequestStatus.REJECTED, "pattern");
        List<RecommendationRequestDto> requests = List.of(
                new RecommendationRequestDto(
                        1L,
                        "message",
                        null,
                        null,
                        2L,
                        3L,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                ),
                new RecommendationRequestDto(
                        10L,
                        "message",
                        null,
                        null,
                        20L,
                        30L,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
        List<RecommendationRequestDto> expectedRequests = List.of(
                new RecommendationRequestDto(
                        1L,
                        "message",
                        null,
                        null,
                        2L,
                        3L,
                        requests.get(0).createdAt(),
                        requests.get(0).updatedAt()
                ),
                new RecommendationRequestDto(
                        10L,
                        "message",
                        null,
                        null,
                        20L,
                        30L,
                        requests.get(1).createdAt(),
                        requests.get(1).updatedAt()
                )
        );
        when(service.getRequests(filter)).thenReturn(requests);

        // Act
        List<RecommendationRequestDto> returnRequests = controller.getRecommendationRequests(filter);
        verify(service, times(1)).getRequests(captorFilter.capture());

        // Assert
        Assertions.assertEquals(expectedFilter, filter);
        Assertions.assertEquals(expectedRequests, returnRequests);
    }

    @Test
    public void testGetRecommendationRequest() {
        // Arrange
        when(service.getRequest(id)).thenReturn(returnDto);

        // Act
        RecommendationRequestDto receivedDto = controller.getRecommendationRequest(id);
        verify(service, times(1)).getRequest(captorId.capture());

        // Assert
        Assertions.assertEquals(expectedId, captorId.getValue());
        Assertions.assertEquals(expectedDto, receivedDto);
    }

    @Test
    public void testRejectRequest() {
        // Arrange
        RejectionDto rejectionDto = new RejectionDto("reason");
        RejectionDto expectedRejectionDto = new RejectionDto("reason");

        // Act
        service.rejectRequest(id, rejectionDto);
        verify(service, times(1)).rejectRequest(captorId.capture(), captorRejection.capture());

        // Assert
        Assertions.assertEquals(expectedRejectionDto, captorRejection.getValue());
        Assertions.assertEquals(expectedId, captorId.getValue());
    }
}
