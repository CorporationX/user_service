package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RecommendationRequestMapperTest {

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Test
    void toRecommendationRequest() {
        RecommendationRequestDto dto = new RecommendationRequestDto();
        dto.setId(1L);
        dto.setMessage("Hello World");
        dto.setRequesterId(100L);
        dto.setReceiverId(200L);
        dto.setSkills(List.of());
        dto.setStatus(RequestStatus.PENDING);

        RecommendationRequest request = recommendationRequestMapper.toRecommendationRequest(dto);

        assertNotNull(request, "Полученный объект не должен быть null");
        assertEquals(dto.getId(), request.getId(), "Проверка идентификатора");
        assertEquals(dto.getMessage(), request.getMessage(), "Проверка сообщения");
        assertNotNull(request.getRequester(), "Объект requester не должен быть null");
        assertEquals(dto.getRequesterId(), request.getRequester().getId(), "Проверка идентификатора requester");
        assertNotNull(request.getReceiver(), "Объект receiver не должен быть null");
        assertEquals(dto.getReceiverId(), request.getReceiver().getId(), "Проверка идентификатора receiver");
        assertEquals(dto.getStatus(), request.getStatus(), "Проверка статуса");
    }

    @Test
    void toRecommendationRequestDto() {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(2L);
        request.setMessage("Test Message");
        request.setStatus(RequestStatus.ACCEPTED);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        User requester = new User();
        requester.setId(300L);
        User receiver = new User();
        receiver.setId(400L);
        request.setRequester(requester);
        request.setReceiver(receiver);

        RecommendationRequestDto dto = recommendationRequestMapper.toRecommendationRequestDto(request);

        assertNotNull(dto, "DTO не должен быть null");
        assertEquals(request.getId(), dto.getId(), "Проверка идентификатора");
        assertEquals(request.getMessage(), dto.getMessage(), "Проверка сообщения");
        assertEquals(request.getStatus(), dto.getStatus(), "Проверка статуса");
        assertNotNull(dto.getRequesterId(), "RequesterId не должен быть null");
        assertEquals(request.getRequester().getId(), dto.getRequesterId(), "Проверка идентификатора requester");
        assertNotNull(dto.getReceiverId(), "ReceiverId не должен быть null");
        assertEquals(request.getReceiver().getId(), dto.getReceiverId(), "Проверка идентификатора receiver");
    }

    @Test
    void toRecommendationRequestDtoList() {
        RecommendationRequest request1 = new RecommendationRequest();
        request1.setId(10L);
        request1.setMessage("Message 10");
        request1.setStatus(RequestStatus.PENDING);
        request1.setCreatedAt(LocalDateTime.now());
        request1.setUpdatedAt(LocalDateTime.now());
        User requester1 = new User();
        requester1.setId(500L);
        request1.setRequester(requester1);
        User receiver1 = new User();
        receiver1.setId(600L);
        request1.setReceiver(receiver1);

        RecommendationRequest request2 = new RecommendationRequest();
        request2.setId(20L);
        request2.setMessage("Message 20");
        request2.setStatus(RequestStatus.REJECTED);
        request2.setCreatedAt(LocalDateTime.now());
        request2.setUpdatedAt(LocalDateTime.now());
        User requester2 = new User();
        requester2.setId(700L);
        request2.setRequester(requester2);
        User receiver2 = new User();
        receiver2.setId(800L);
        request2.setReceiver(receiver2);

        List<RecommendationRequest> requestList = List.of(request1, request2);

        List<RecommendationRequestDto> dtoList = recommendationRequestMapper.toRecommendationRequestDtoList(requestList);

        assertNotNull(dtoList, "Список DTO не должен быть null");
        assertEquals(requestList.size(), dtoList.size(), "Проверка размера списка DTO");

        for (int i = 0; i < requestList.size(); i++) {
            RecommendationRequest req = requestList.get(i);
            RecommendationRequestDto dto = dtoList.get(i);
            assertEquals(req.getId(), dto.getId(), "Проверка идентификатора для элемента " + i);
            assertEquals(req.getMessage(), dto.getMessage(), "Проверка сообщения для элемента " + i);
            assertEquals(req.getStatus(), dto.getStatus(), "Проверка статуса для элемента " + i);
            assertNotNull(dto.getRequesterId(), "RequesterId не должен быть null для элемента " + i);
            assertEquals(req.getRequester().getId(), dto.getRequesterId(), "Проверка requesterId для элемента " + i);
            assertNotNull(dto.getReceiverId(), "ReceiverId не должен быть null для элемента " + i);
            assertEquals(req.getReceiver().getId(), dto.getReceiverId(), "Проверка receiverId для элемента " + i);
        }
    }
}