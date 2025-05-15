package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationResponseDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.recommendation.RecommendationRequestException;
import school.faang.user_service.exception.recommendation.RecommendationRequestNotFoundException;
import school.faang.user_service.exception.recommendation.RecommendationRequestValidationException;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterByMessagePattern;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterByReceiverId;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterByRequesterId;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilterByStatus;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillService;
import school.faang.user_service.service.UserService;
import school.faang.user_service.utils.Utils;
import school.faang.user_service.validator.Validator;
import school.faang.user_service.validator.recommendation.MessageValidator;
import school.faang.user_service.validator.recommendation.PersonValidator;
import school.faang.user_service.validator.recommendation.RejectValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository requestRepository;
    @Spy
    private RecommendationRequestMapperImpl mapper;
    @Spy
    private Utils utils;

    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters = List.of(
            new RecommendationRequestFilterByMessagePattern(),
            new RecommendationRequestFilterByRequesterId(),
            new RecommendationRequestFilterByReceiverId(),
            new RecommendationRequestFilterByStatus()
    );

    private final List<Validator<RecommendationRequestDto>> requestValidators = List.of(
            new MessageValidator(), new PersonValidator()
    );

    private final List<Validator<RejectionDto>> rejectValidators = List.of(
            new RejectValidator()
    );

    @Mock
    private UserService userService;
    @Mock
    private SkillService skillService;

    private RecommendationRequestService requestService;

    @BeforeEach
    public void setUpService() {
        requestService = new RecommendationRequestService(
                userService, skillService, requestRepository, mapper, filters,
                requestValidators, rejectValidators, utils
        );
    }

    @Test
    public void testCreatingMethod() {
        final Long requestId = 1L;
        final Long requesterId = 2L;
        final Long receiverId = 3L;

        List<Skill> mockSkills = List.of(getMockSkill(1L), getMockSkill(2L), getMockSkill(3L));

        RecommendationRequestDto mockDto = getMockDto(null, requesterId, receiverId);
        LongStream.range(1, 4).forEach(mockDto::addSkill);

        User requester = getMockUser(requesterId);
        User receiver = getMockUser(receiverId);
        RecommendationRequest resultMockEntity = getMockEntity(requestId, RequestStatus.PENDING, requester, receiver);

        when(skillService.getSkillsByIds(any())).thenReturn(mockSkills);
        when(userService.getUserById(mockDto.getRequesterId())).thenReturn(requester);
        when(userService.getUserById(mockDto.getReceiverId())).thenReturn(receiver);
        when(requestRepository.save(any())).thenReturn(resultMockEntity);

        RecommendationResponseDto resultDto = requestService.create(mockDto);
        verify(requestRepository, times(1)).save(any());

        assertNotNull(resultDto);
        assertEquals(requestId, resultDto.getId());
    }

    @Test
    public void testCreatingWithSkillsException() {
        final Long requesterId = 2L;
        final Long receiverId = 3L;

        List<Skill> mockSkills = List.of(
                getMockSkill(1L),
                getMockSkill(2L),
                getMockSkill(3L),
                getMockSkill(4L)
        );
        RecommendationRequestDto mockDto = getMockDto(null, requesterId, receiverId);
        LongStream.range(1, 4).forEach(mockDto::addSkill);

        when(skillService.getSkillsByIds(any())).thenReturn(mockSkills);

        RecommendationRequestException result = assertThrows(
                RecommendationRequestException.class, () -> requestService.create(mockDto));
        verify(requestRepository, times(0)).save(any());
        assertEquals(RecommendationRequestService.SKILLS_MISSING_FROM_DATABASE, result.getMessage());
    }

    @Test
    public void testExceptionForRepeatRequest() {
        final Long requestId = 1L;
        final Long requesterId = 2L;
        final Long receiverId = 3L;

        RecommendationRequestDto mockDto = getMockDto(requestId, requesterId, receiverId);
        when(requestRepository.countRepeatedRequest(mockDto.getRequesterId(), mockDto.getReceiverId()))
                .thenReturn(1);

        RecommendationRequestValidationException result = assertThrows(
                RecommendationRequestValidationException.class, () -> requestService.create(mockDto));
        verify(requestRepository, times(0)).save(any());
        assertEquals(RecommendationRequestService.SIX_MONTHS_PERIOD_ERROR, result.getMessage());
    }

    @Test
    @DisplayName("testing getRequests and receive EMPTY list")
    public void testGetEmptyRequests() {
        RequestFilterDto filterDto = RequestFilterDto.builder().build();

        when(requestRepository.findAll()).thenReturn(Collections.emptyList());
        List<RecommendationResponseDto> resultDtoList = requestService.getRequests(filterDto);

        assertNotNull(resultDtoList, "requestService.getRequests() return NULL.");
        assertTrue(resultDtoList.isEmpty());
    }

    @Test
    @DisplayName("testing getRequests and receive NO empty list")
    public void testGetRequests() {
        RequestFilterDto filterDto = RequestFilterDto.builder()
                .status(RequestStatus.PENDING)
                .build();
        List<RecommendationRequest> resultEntity = List.of(
                getMockEntity(1L, RequestStatus.PENDING, getMockUser(1L), getMockUser(2L)),
                getMockEntity(2L, RequestStatus.REJECTED, getMockUser(1L), getMockUser(3L)),
                getMockEntity(3L, RequestStatus.PENDING, getMockUser(2L), getMockUser(3L))
        );

        when(requestRepository.findAll()).thenReturn(resultEntity);
        List<RecommendationResponseDto> resultDtoList = requestService.getRequests(filterDto);

        assertNotNull(resultDtoList, "requestService.getRequests() return NULL.");
        assertFalse(resultDtoList.isEmpty());
    }

    @Test
    public void testGetRequestById() {
        Long requestId = 10L;
        User requester = getMockUser(20L);
        User receiver = getMockUser(30L);
        Optional<RecommendationRequest> resultMockEntity =
                Optional.of(getMockEntity(requestId, RequestStatus.PENDING, requester, receiver));

        when(requestRepository.findById(10L)).thenReturn(resultMockEntity);
        RecommendationResponseDto response = requestService.getRequest(requestId);

        assertNotNull(response);
        assertEquals(requestId, response.getId());
    }

    @Test
    public void testGetEmptyRequest() {
        Long requestId = 10L;
        Optional<RecommendationRequest> resultMockEntity = Optional.empty();
        String expected = utils.format(RecommendationRequestService.REQUEST_BY_ID_NOT_FOUND, requestId);

        when(requestRepository.findById(10L)).thenReturn(resultMockEntity);

        RecommendationRequestNotFoundException result = assertThrows(
                RecommendationRequestNotFoundException.class, () -> requestService.getRequest(requestId));
        assertEquals(expected, result.getMessage());
    }

    @Test
    public void testRejectRequestSuccess() {
        final Long requestId = 10L;
        final Long requesterId = 20L;
        final Long receiverId = 25L;

        String reason = "simple reason";
        final RejectionDto rejection = new RejectionDto(reason);

        User requester = getMockUser(requesterId);
        User receiver = getMockUser(receiverId);
        Optional<RecommendationRequest> findEntity = Optional.of(
                getMockEntity(requestId, RequestStatus.PENDING, requester, receiver));
        RecommendationRequest resultEntity = getMockEntity(requestId, RequestStatus.REJECTED, requester, receiver);
        resultEntity.setRejectionReason(reason);
        LongStream.range(1, 4)
                .forEach(value -> resultEntity.addSkillRequest(getSkillRequest(value, value + 1)));

        when(requestRepository.findById(requestId)).thenReturn(findEntity);
        when(requestRepository.save(findEntity.get())).thenReturn(resultEntity);

        RecommendationResponseDto resultDto = requestService.rejectRequest(requestId, rejection);
        verify(requestRepository, times(1)).save(findEntity.get());
        assertEquals(requestId, resultDto.getId());
        assertEquals(reason, resultDto.getRejectionReason());
    }

    @Test
    public void testRejectRequestNotFoundById() {
        final Long requestId = 1001L;

        String reason = "simple reason";
        RejectionDto rejection = new RejectionDto(reason);
        Optional<RecommendationRequest> findEntity = Optional.empty();
        String expected = utils.format(RecommendationRequestService.REQUEST_BY_ID_NOT_FOUND, requestId);

        when(requestRepository.findById(requestId)).thenReturn(findEntity);

        RecommendationRequestNotFoundException result = assertThrows(RecommendationRequestNotFoundException.class,
                () -> requestService.rejectRequest(requestId, rejection));
        verify(requestRepository, times(0)).save(any());
        assertEquals(expected, result.getMessage());
    }

    @Test
    public void testRejectRequestByWrongStatus() {
        final Long requestId = 1001L;
        final Long requesterId = 20L;
        final Long receiverId = 25L;
        final RejectionDto rejection = new RejectionDto("bla-bla");

        User requester = getMockUser(requesterId);
        User receiver = getMockUser(receiverId);
        Optional<RecommendationRequest> findEntity = Optional.of(
                getMockEntity(requestId, RequestStatus.REJECTED, requester, receiver));
        String expected = utils.format(
                RecommendationRequestService.STATUS_HAS_NOT_BEEN_CHANGED,
                requestId,
                RecommendationRequestService.CHECK_STATUS_FOR_REJECT
        );

        when(requestRepository.findById(requestId)).thenReturn(findEntity);

        RecommendationRequestException result = assertThrows(RecommendationRequestException.class,
                () -> requestService.rejectRequest(requestId, rejection));
        verify(requestRepository, times(0)).save(any());
        assertEquals(expected, result.getMessage());
    }

    private static SkillRequest getSkillRequest(Long skillRequestId, Long skillId) {
        return SkillRequest.builder()
                .id(skillRequestId)
                .skill(getMockSkill(skillId))
                .build();
    }

    private static RecommendationRequest getMockEntity(Long id, RequestStatus status, User requester, User receiver) {
        RecommendationRequest resultEntity = RecommendationRequest.builder()
                .requester(requester)
                .receiver(receiver)
                .status(status)
                .build();
        if (id != null) {
            resultEntity.setId(id);
        }
        return resultEntity;
    }

    private static User getMockUser(Long id) {
        return User.builder()
                .id(id)
                .username("mockUser%s".formatted(id))
                .build();
    }

    private static RecommendationRequestDto getMockDto(Long requestId, Long requesterId, Long receiverId) {
        return RecommendationRequestDto.builder()
                .id(requestId)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .message("request message")
                .build();
    }

    private static Skill getMockSkill(Long skillId) {
        return Skill.builder()
                .id(skillId)
                .build();
    }
}