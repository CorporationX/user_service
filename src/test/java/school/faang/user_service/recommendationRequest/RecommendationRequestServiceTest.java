package school.faang.user_service.recommendationRequest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationRequestDto;
import school.faang.user_service.controller.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestFilterMapperImpl;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.mapper.RecommendationRequestRejectionMapperImpl;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.RecommendationRequestFilter;
import school.faang.user_service.service.RecommendationRequestService;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;
    @Mock
    private SkillRequestRepository skillRequestRepository;
    @Spy
    private RecommendationRequestMapperImpl recommendationRequestMapper;
    @Spy
    private RecommendationRequestFilterMapperImpl recommendationRequestFilterMapper;
    @Spy
    private RecommendationRequestRejectionMapperImpl recommendationRequestRejectionMapper;
    @Mock
    private List<RecommendationRequestFilter> filters;
    @InjectMocks
    private RecommendationRequestService recommendationRequestService;
    @Captor
    private ArgumentCaptor<RecommendationRequest> captor;

    @Test
    public void testValidateReceiverExistence() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        when(recommendationRequestRepository.existsById(any())).thenReturn(false);
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateRequesterExistence() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        requestDto.setRequesterId(-2L);
        requestDto.setReceiverId(-1L);
        when(recommendationRequestRepository.existsById(-1L)).thenReturn(true);
        when(recommendationRequestRepository.existsById(-2L)).thenReturn(false);
        assertThrows(RuntimeException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateRequestsPeriod() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        assertThrows(RuntimeException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testValidateSkillsExistence() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        requestDto.setUpdatedAt(LocalDateTime.of(2024, Month.NOVEMBER, 1, 13, 6));
        requestDto.setSkillsIds(List.of(1L, 2L));
        assertThrows(RuntimeException.class, () -> recommendationRequestService.create(requestDto));
    }

    @Test
    public void testRecommendationRequestDtoCreate() {
        RecommendationRequestDto requestDto = validateUserExistenceAndPeriod();
        requestDto.setUpdatedAt(LocalDateTime.of(2024, Month.NOVEMBER, 1, 13, 6));
        requestDto.setSkillsIds(List.of(1L, 2L));
        requestDto.setMessage("Test");
        requestDto.setRequesterId(1L);
        requestDto.setReceiverId(2L);
        when(skillRequestRepository.findAllById(any())).thenReturn(List.of(new SkillRequest(), new SkillRequest()));
        when(recommendationRequestRepository.getReferenceById(any())).thenReturn(new RecommendationRequest());
        when(skillRequestRepository.findAllById(any())).thenReturn(List.of(new SkillRequest(), new SkillRequest()));
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setMessage(requestDto.getMessage());
        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        when(recommendationRequestRepository.save(any())).thenReturn(recommendationRequest);
        recommendationRequestService.create(requestDto);
        verify(recommendationRequestRepository, times(1)).save(captor.capture());
    }

    @Test
    public void testRequestValidation() {
        when(recommendationRequestRepository.existsById(any())).thenReturn(false);
        assertThrows(RuntimeException.class, () -> recommendationRequestService.getRequest(any()));
    }

    @Test
    public void testGetRequest() {
        when(recommendationRequestRepository.existsById(1L)).thenReturn(true);
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setId(1L);
        recommendationRequest.setSkills(List.of(new SkillRequest(), new SkillRequest()));
        when(recommendationRequestRepository.getReferenceById(any())).thenReturn(recommendationRequest);
        recommendationRequestService.getRequest(1L);
        verify(recommendationRequestMapper, times(1)).toDto(recommendationRequest);
    }

    @Test
    public void testRequestsByFilter() {
        RequestFilterDto filterDto = new RequestFilterDto();
        filterDto.setRecommendationId(1L);
        filterDto.setRequesterName("John");
        filterDto.setReceiverName("Jack");
        filterDto.setStatus(RequestStatus.PENDING);
        filterDto.setMessage("Test");
        filterDto.setRejectionReason("Test");
        List<RecommendationRequest> list = List.of(new RecommendationRequest(), new RecommendationRequest());
//        RecommendationRequest firstRequest = new RecommendationRequest(1L,new User(),new User(),"Test",)
        when(recommendationRequestRepository.findAll()).thenReturn(list);
        recommendationRequestService.getRequestsByFilter(filterDto);
        verify(recommendationRequestFilterMapper, times(list.size())).toDto(new RecommendationRequest());
    }

    private RecommendationRequestDto validateUserExistenceAndPeriod() {
        RecommendationRequestDto requestDto = new RecommendationRequestDto();
        LocalDateTime createdDate = LocalDateTime.of(2024, Month.FEBRUARY, 1, 13, 7);
        LocalDateTime updatedDate = LocalDateTime.of(2024, Month.AUGUST, 1, 13, 6);
        requestDto.setCreatedAt(createdDate);
        requestDto.setUpdatedAt(updatedDate);
        when(recommendationRequestRepository.existsById(any())).thenReturn(true);

        return requestDto;
    }
}
