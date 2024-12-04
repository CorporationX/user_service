package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.RecommendationRequestValidationException;
import school.faang.user_service.exception.RejectRequestFailedException;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@ActiveProfiles("test")
class RecommendationRequestServiceTest {
    @Autowired
    private RecommendationRequestService recommendationRequestService;

    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @Autowired
    private List<RecommendationRequestFilter> recRequestFilters;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private SkillRequestRepository skillRequestRepository;

    @Value("${application.constants.max-month-limit-recommendation-request}")
    private int maxMonthLimitRecommendationRequest;

    @Test
    void testCreateRecommendationRequestRequesterNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto(1L, 2L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.create(dto));

        verify(userRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testCreateRecommendationRequestReceiverNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto(1L, 2L);
        User requester = createUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.create(dto));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testCreateRecRequestWhenSkillsNotFound() {
        RecommendationRequestDto dto = createRecommendationRequestDto(1L, 2L);
        dto.setCreatedAt(LocalDateTime.of(2024, 3, 30, 12, 22));
        dto.setSkillIds(List.of(1L, 2L, 3L));
        User requester = createUser(dto.getRequesterId());
        User receiver = createUser(dto.getReceiverId());
        List<Skill> skills = List.of(createSkill(1), createSkill(2));
        RecommendationRequest recommendationRequest = createRecommendationRequest(requester, receiver, dto.getCreatedAt());
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(dto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        when(skillRepository.findAllById(dto.getSkillIds())).thenReturn(skills);

        assertThrows(RecommendationRequestValidationException.class, () -> recommendationRequestService.create(dto));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(recommendationRequestRepository, times(1)).findAll();
        verify(skillRepository, times(1)).findAllById(dto.getSkillIds());
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);
    }

    @Test
    void testCreateRecommendationRequestMessageIsEmpty() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        LocalDateTime now = LocalDateTime.now();
        List<Skill> skills = List.of(createSkill(1L), createSkill(2L));
        RecommendationRequestDto dto = createRecommendationRequestDto(requester.getId(), receiver.getId());
        dto.setMessage("");
        dto.setSkillIds(List.of(1L, 2L));
        RecommendationRequest recommendationRequest = createRecommendationRequest(requester, receiver, now);
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(dto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        when(skillRepository.findAllById(dto.getSkillIds())).thenReturn(skills);

        assertThrows(RecommendationRequestValidationException.class, () -> recommendationRequestService.create(dto));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(skillRepository, times(1)).findAllById(dto.getSkillIds());
        verifyNoMoreInteractions(userRepository, skillRepository);
    }

    @Test
    void testCreateRecRequestSendingRequestNotAllowed() {
        RecommendationRequestDto dto = createRecommendationRequestDto(1L, 2L);
        dto.setCreatedAt(LocalDateTime.of(2024, 6, 30, 12, 22));
        User requester = createUser(dto.getRequesterId());
        User receiver = createUser(dto.getReceiverId());
        RecommendationRequest recommendationRequest = createRecommendationRequest(requester, receiver, dto.getCreatedAt());
        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(dto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));

        RecommendationRequestDto newRequestDto = createRecommendationRequestDto(1L, 2L);
        newRequestDto.setCreatedAt(LocalDateTime.of(2024, 11, 4, 12, 22));

        assertThrows(RecommendationRequestValidationException.class, () -> recommendationRequestService.create(newRequestDto));

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(recommendationRequestRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository);
    }

    @Test
    void testCreateRecommendationRequestSuccessfully() {
        RecommendationRequestDto dto = createRecommendationRequestDto(1L, 2L);
        User requester = createUser(dto.getRequesterId());
        User receiver = createUser(dto.getReceiverId());
        dto.setMessage("Please, accept my request!");
        dto.setCreatedAt(LocalDateTime.of(2024, 3, 30, 12, 22));
        dto.setUpdatedAt(dto.getCreatedAt());
        dto.setSkillIds(List.of(1L, 2L, 3L));
        dto.setStatus(RequestStatus.PENDING);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);
        recommendationRequest.setStatus(RequestStatus.PENDING);

        when(userRepository.findById(1L)).thenReturn(Optional.of(requester));
        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(recommendationRequestMapper.toEntity(dto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.findAll()).thenReturn(List.of(recommendationRequest));
        when(skillRepository.findAllById(dto.getSkillIds())).thenReturn(List.of(createSkill(1), createSkill(2), createSkill(3)));
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);

        RecommendationRequestDto requestFromDB = recommendationRequestService.create(dto);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(recommendationRequestRepository, times(1)).findAll();
        verify(skillRepository, times(1)).findAllById(dto.getSkillIds());
        verify(recommendationRequestRepository, times(1)).save(any(RecommendationRequest.class));
        verifyNoMoreInteractions(userRepository, recommendationRequestRepository, skillRepository);

        assertNotNull(requestFromDB);
        assertEquals(dto.getRequesterId(), requestFromDB.getRequesterId());
        assertEquals(dto.getReceiverId(), requestFromDB.getReceiverId());
        assertEquals(dto.getMessage(), requestFromDB.getMessage());
        assertEquals(dto.getStatus(), requestFromDB.getStatus());
        assertEquals(dto.getCreatedAt(), requestFromDB.getCreatedAt());
    }

    @Test
    void testGetRecommendationRequestsMessageNotNull() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setMessagePattern("Please, accept my request!");
        List<RecommendationRequest> recommendationRequests = getRecommendationRequests();

        when(recommendationRequestRepository.findAll()).thenReturn(recommendationRequests);
        List<RecommendationRequestDto> requestsDto = recommendationRequestService.getRequests(filter);

        verify(recommendationRequestRepository, times(1)).findAll();
        verifyNoMoreInteractions(recommendationRequestRepository);

        assertEquals(filter.getMessagePattern(), requestsDto.get(0).getMessage());
    }

    @Test
    void testGetRecommendationRequestsAllNull() {
        RequestFilterDto filter = new RequestFilterDto();
        List<RecommendationRequest> requests = getRecommendationRequests();

        when(recommendationRequestRepository.findAll()).thenReturn(requests);
        List<RecommendationRequestDto> requestsDtos = recommendationRequestService.getRequests(filter);

        verify(recommendationRequestRepository, times(1)).findAll();
        verifyNoMoreInteractions(recommendationRequestRepository);

        assertNotEquals(filter.getMessagePattern(), requestsDtos.get(0).getMessage());
        assertNotEquals(filter.getCreatedAt(), requestsDtos.get(0).getCreatedAt());
        assertNotEquals(filter.getRequesterId(), requestsDtos.get(0).getRequesterId());
        assertNotEquals(filter.getReceiverId(), requestsDtos.get(0).getReceiverId());
    }

    @Test
    void testGetRecommendationRequestsSumValuesNull() {
        RequestFilterDto filter = new RequestFilterDto();
        filter.setRequesterId(1L);
        filter.setCreatedAt(LocalDateTime.of(2024, 3, 30, 12, 22));
        List<RecommendationRequest> requests = getRecommendationRequests();
        when(recommendationRequestRepository.findAll()).thenReturn(requests);

        List<RecommendationRequestDto> requestDtos = recommendationRequestService.getRequests(filter);

        verify(recommendationRequestRepository, times(1)).findAll();
        verifyNoMoreInteractions(recommendationRequestRepository);

        assertEquals(filter.getRequesterId(), requestDtos.get(0).getRequesterId());
        assertEquals(filter.getCreatedAt(), requestDtos.get(0).getCreatedAt());
        assertNotEquals(filter.getReceiverId(), requestDtos.get(0).getReceiverId());
        assertNotEquals(filter.getMessagePattern(), requestDtos.get(0).getMessage());
    }

    @Test
    void testGetRecommendationRequestNull() {
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.getRequest(1L));

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    void testGetRecommendationRequestSuccessfully() {
        RecommendationRequest request = new RecommendationRequest();
        request.setId(1L);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        RecommendationRequestDto requestDto = recommendationRequestService.getRequest(1L);

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(recommendationRequestRepository);

        assertNotNull(requestDto);
        assertEquals(request.getId(), requestDto.getId());
    }

    @Test
    void testRejectRecommendationRequestEntityNotFound() {
        RejectionDto dto = new RejectionDto();
        dto.setId(1L);
        dto.setRequesterId(1L);
        dto.setReceiverId(2L);
        dto.setMessage("message");
        dto.setStatus(RequestStatus.PENDING);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationRequestService.rejectRequest(1L, dto));

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    void testRejectRecommendationRequestWithoutPendingStatus() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setId(1L);
        rejectionDto.setRequesterId(2L);
        rejectionDto.setReceiverId(3L);
        rejectionDto.setReason("I don't want to approve that request");
        rejectionDto.setStatus(RequestStatus.REJECTED);
        RecommendationRequest recommendationRequest = createRecommendationRequest(requester, receiver, rejectionDto.getCreatedAt());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toEntity(rejectionDto)).thenReturn(recommendationRequest);

        assertThrows(RejectRequestFailedException.class, () -> recommendationRequestService.rejectRequest(1L, rejectionDto));

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(recommendationRequestRepository);
    }

    @Test
    void testRejectRecommendationRequestSuccessfully() {
        User requester = createUser(1L);
        User receiver = createUser(2L);
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setId(1L);
        rejectionDto.setRequesterId(requester.getId());
        rejectionDto.setReceiverId(receiver.getId());
        rejectionDto.setMessage("Please, approve my request");
        rejectionDto.setReason("I don't want to approve that request");
        rejectionDto.setStatus(RequestStatus.PENDING);
        RecommendationRequest recommendationRequest = createRecommendationRequest(requester, receiver, rejectionDto.getCreatedAt());
        recommendationRequest.setId(rejectionDto.getId());
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        when(recommendationRequestRepository.findById(1L)).thenReturn(Optional.of(recommendationRequest));
        when(recommendationRequestMapper.toEntity(rejectionDto)).thenReturn(recommendationRequest);
        when(recommendationRequestRepository.save(any(RecommendationRequest.class))).thenReturn(recommendationRequest);

        RejectionDto result = recommendationRequestService.rejectRequest(1L, rejectionDto);

        verify(recommendationRequestRepository, times(1)).findById(1L);
        verify(recommendationRequestRepository, times(1)).save(any(RecommendationRequest.class));
        verifyNoMoreInteractions(recommendationRequestRepository);

        assertNotNull(result);
        assertEquals(result.getRequesterId(), rejectionDto.getRequesterId());
        assertEquals(result.getReceiverId(), rejectionDto.getReceiverId());
        assertEquals(result.getCreatedAt(), rejectionDto.getCreatedAt());
        assertNotEquals(result.getStatus(), rejectionDto.getStatus());
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private RecommendationRequestDto createRecommendationRequestDto(Long requesterId, Long receiverId) {
        return new RecommendationRequestDto(null, null, null, null, requesterId, receiverId, null, null);
    }

    private RecommendationRequest createRecommendationRequest(User requester, User receiver, LocalDateTime createdAt) {
        return new RecommendationRequest(0, requester, receiver, null, null, null, null, null, createdAt, null);
    }

    private Skill createSkill(long id) {
        return new Skill(id, null, null, null, null, null, null, null);
    }

    private List<RecommendationRequest> getRecommendationRequests() {
        User firstUser = createUser(1L);
        User secondUser = createUser(2L);
        LocalDateTime createdAt = LocalDateTime.of(2024, 3, 30, 12, 22);
        String requestMessage = "Please, accept my request!";

        RecommendationRequest firstRequest = createRecommendationRequest(firstUser, secondUser, createdAt);
        firstRequest.setMessage(requestMessage);

        RecommendationRequest secondRequest = createRecommendationRequest(secondUser, firstUser, createdAt);
        secondRequest.setMessage(requestMessage);

        return new ArrayList<>(List.of(firstRequest, secondRequest));
    }
}
