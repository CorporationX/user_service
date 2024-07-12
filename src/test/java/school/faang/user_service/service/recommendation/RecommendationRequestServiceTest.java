package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.validator.ValidatorForRecommendationRequestService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {

    @Spy
    private RecommendationRequestMapper requestMapper = Mappers.getMapper(RecommendationRequestMapper.class);
    @Mock
    private RecommendationRequestRepository requestRepository;
    @Mock
    private ValidatorForRecommendationRequestService validator;

    @InjectMocks
    private RecommendationRequestService service;

    @Test
    void testCreateSaveRecommendationRequest() {
        var dto = createDto();
        var entity = createEntity();

        when(requestRepository.save(entity)).thenReturn(entity);
        when(requestMapper.toEntityList(dto)).thenReturn(entity);
        when(requestMapper.toDtoList(entity)).thenReturn(dto);

        var returnDto = service.create(dto);

        verify(validator, times(1)).validatorData(dto);
        verify(requestRepository, times(1)).save(entity);
        assertEquals(dto, returnDto);
    }

    @Test
    void testGetRequestSuccessfulApply() {
        var dto = createDto();
        var entity = createEntity();
        when(requestRepository.findById(dto.getId())).thenReturn(Optional.of(entity));

        var resultDto = service.getRequest(dto.getId());

        assertEquals(dto, resultDto);
    }

    @Test
    void testGetRequestNotFoundDataBase() {
        var dto = createDto();
        when(requestRepository.findById(dto.getId())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.getRequest(dto.getId()));
    }

    @Test
    void testRejectRejectSuccessfulApply() {
        var dto = createDto();
        var entity = createEntity();
        dto.setStatus(RequestStatus.PENDING);
        entity.setStatus(RequestStatus.PENDING);
        when(requestRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        String reason = "reason";
        var reject = RequestStatus.REJECTED;
        var examinationEntity = createEntity();
        examinationEntity.setStatus(reject);
        examinationEntity.setRejectionReason(reason);
        var examinationStatus = reject;
        var examinationDto = dto;
        examinationDto.setRejectionReason(reason);
        examinationDto.setStatus(examinationStatus);
        when(requestRepository.save(entity)).thenReturn(examinationEntity);

        var resultDto = service.rejectRequest(dto.getId(), reason);

        assertEquals(reason, resultDto.getRejectionReason());
        assertEquals(examinationStatus, resultDto.getStatus());
        verify(requestRepository, times(1)).save(entity);
    }

    @Test
    void testRejectRequestNotFoundDataBase() {
        var dto = createDto();
        when(requestRepository.findById(dto.getId()))
                .thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service.rejectRequest(dto.getId(), "reason"));
    }

    @Test
    void testRejectRequestAlreadyBeenReject() {
        var dto = createDto();
        when(requestRepository.findById(dto.getId()))
                .thenReturn(Optional.of(requestMapper.toEntityList(dto)));
        dto.setStatus(RequestStatus.REJECTED);

        var resultDto = service.rejectRequest(dto.getId(), "reason");

        assertEquals(dto, resultDto);
    }

    @Test
    void testGetRequestsSuccessfulApplyForEmptyFilter() {
        var listEntity = createListDto();
        when(requestRepository.findAll()).thenReturn(listEntity);

        var resultListEntity = service.getRequests(new RequestFilterDto());

        assertEquals(listEntity, resultListEntity);
    }

    @Test
    void testDetRequestSuccessfulApplyForOneFieldFilter() {
        var listEntity = createListDto();
        listEntity.forEach(entity -> entity.setStatus(RequestStatus.PENDING));
        when(requestRepository.findAll()).thenReturn(listEntity);
        var filterWithOneField = new RequestFilterDto(RequestStatus.PENDING, null);
        var examineListDto = listEntity.stream().map(entity -> requestMapper.toDtoList(entity)).toList();

        var resultListDto = service.getRequests(filterWithOneField);

        assertEquals(examineListDto, resultListDto);
    }

    @Test
    void testDetRequestSuccessfulApplyForTwoFieldFilter() {
        var listEntity = createListDto();
        listEntity.forEach(entity -> entity.setStatus(RequestStatus.PENDING));
        when(requestRepository.findAll()).thenReturn(listEntity);
        var filterWithTwoField = new RequestFilterDto(RequestStatus.PENDING, 1L);
        var examineDto = createDto();
        examineDto.setStatus(RequestStatus.PENDING);
        var examineListDto = List.of(examineDto);

        var resultListDto = service.getRequests(filterWithTwoField);

        assertEquals(examineListDto, resultListDto);
    }

    @Test
    void testDetRequestForEmptyList() {
        var dto = createDto();
        var listEntity = createListDto();
        listEntity.forEach(entity -> entity.setStatus(RequestStatus.REJECTED));
        when(requestRepository.findAll()).thenReturn(listEntity);
        var filterWithOneField = new RequestFilterDto(RequestStatus.PENDING, 1L);

        assertThrows(NoSuchElementException.class, () -> service.getRequests(filterWithOneField));
    }

    private RecommendationRequestDto createDto() {
        var dto = new RecommendationRequestDto();
        dto.setId(1L);
        dto.setRecieverId(2L);
        dto.setRequesterId(3L);
        dto.setMessage("message");
        dto.setSkills(List.of(new SkillRequestDto(), new SkillRequestDto()));
        dto.setUpdatedAt(LocalDateTime.now().minusMonths(7));
        return dto;
    }

    private RecommendationRequest createEntity() {
        var requester = new User();
        var reciever = new User();
        requester.setId(3L);
        reciever.setId(2L);
        var entity = new RecommendationRequest();
        entity.setId(1L);
        entity.setRequester(requester);
        entity.setRequester(reciever);
        entity.setMessage("message");
        entity.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        entity.setUpdatedAt(LocalDateTime.now().minusMonths(7));
        return entity;
    }

    private List<RecommendationRequest> createListDto() {
        List<RecommendationRequest> listEntity = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            var dto = createDto();
            dto.setId((long) i);
            listEntity.add(requestMapper.toEntityList(dto));
        }
        return listEntity;
    }
}