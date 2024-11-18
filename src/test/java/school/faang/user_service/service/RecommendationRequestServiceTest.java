package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filters.recommendation.request.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {
    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillRequestRepository skillRequestRepository;

    @Spy
    private RecommendationRequestMapperImpl requestMapper;

    @Mock
    private List<RecommendationRequestFilter> recRequestFilters;

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    private RecommendationRequestDto recRequestDto;
    private RecommendationRequest recRequestEntity;
    private RequestFilterDto filter;

    @BeforeEach
    public void setUp() {
        recRequestDto = new RecommendationRequestDto();
        recRequestDto.setId(0L);
        long requesterId = 100L;
        recRequestDto.setRequesterId(requesterId);
        long receiverId = 200L;
        recRequestDto.setReceiverId(receiverId);
        recRequestDto.setSkillsId(List.of(11L, 12L, 14L));
        recRequestDto.setMessage("Some message");
        recRequestDto.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        recRequestDto.setUpdatedAt(LocalDateTime.now().minusMinutes(1));
        recRequestEntity = new RecommendationRequest();
        recRequestEntity.setCreatedAt(LocalDateTime.now().minusMonths(7));
        filter = new RequestFilterDto();
    }

    @Test
    public void testUsersExistInDbPositive() {
        when(recommendationRequestRepository
                .checkTheUsersExistInDb(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(true);

        recommendationRequestService.usersExistInDb(recRequestDto);
        verify(recommendationRequestRepository, times(1))
                .checkTheUsersExistInDb(recRequestDto.getRequesterId(), recRequestDto.getReceiverId());
    }

    @Test
    public void testUsersExistInDbNegative() {
        when(recommendationRequestRepository
                .checkTheUsersExistInDb(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () -> recommendationRequestService.usersExistInDb(recRequestDto));
        verify(recommendationRequestRepository, times(1))
                .checkTheUsersExistInDb(recRequestDto.getRequesterId(), recRequestDto.getReceiverId());
    }

    @Test
    public void testSixMonthHavePassedPositive() {
        when(recommendationRequestRepository
                .findLatestPendingRequest(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(Optional.of(recRequestEntity));

        Assertions.assertTrue(recommendationRequestService.sixMonthHavePassed(recRequestDto));
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(recRequestDto.getRequesterId(), recRequestDto.getReceiverId());
    }

    @Test
    public void testSixMonthHavePassedNegative() {
        recRequestEntity.setCreatedAt(LocalDateTime.now().minusMonths(4));
        when(recommendationRequestRepository
                .findLatestPendingRequest(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(Optional.of(recRequestEntity));

        Assertions.assertFalse(recommendationRequestService.sixMonthHavePassed(recRequestDto));
        verify(recommendationRequestRepository, times(1))
                .findLatestPendingRequest(recRequestDto.getRequesterId(), recRequestDto.getReceiverId());
    }

    @Test
    public void testSkillsExistInDbPositive() {
        when(skillRepository.countExisting(recRequestDto
                .getSkillsId()))
                .thenReturn(recRequestDto.getSkillsId().size());

        Assertions.assertTrue(recommendationRequestService.skillsExistInDb(recRequestDto));
    }

    @Test
    public void testSkillsExistInDbNegative() {
        when(skillRepository.countExisting(recRequestDto
                .getSkillsId()))
                .thenReturn(recRequestDto.getSkillsId().size() - 1);

        Assertions.assertFalse(recommendationRequestService.skillsExistInDb(recRequestDto));
    }

    @Test
    public void testCreate() {
        when(recommendationRequestRepository
                .checkTheUsersExistInDb(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(true);

        when(skillRepository.countExisting(recRequestDto
                .getSkillsId()))
                .thenReturn(recRequestDto.getSkillsId().size());

        when(recommendationRequestRepository
                .findLatestPendingRequest(recRequestDto.getRequesterId(), recRequestDto.getReceiverId()))
                .thenReturn(Optional.of(recRequestEntity));

        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1100L);

        RecommendationRequestDto resultRecRequestDto = recommendationRequestService.create(recRequestDto);

        verify(recommendationRepository, times(1))
                .create(recRequestDto.getRequesterId(), recRequestDto.getReceiverId(), resultRecRequestDto.getMessage());
        verify(skillRequestRepository, times(recRequestDto.getSkillsId().size())).create(anyLong(), anyLong());
    }

    @Test
    public void testGetRequest() {
        Mockito.when(recommendationRequestRepository.findById(recRequestDto.getId()))
                .thenReturn(Optional.ofNullable(recRequestEntity));

        recommendationRequestService.getRequest(recRequestDto.getId());

        verify(recommendationRequestRepository, times(1))
                .findById(recRequestDto.getId());
    }

    @Test
    public void rejectRequests() {
        String rejectionReason = "Some reason";
        RejectionDto rejectionDto = new RejectionDto();
        rejectionDto.setReason(rejectionReason);

        Mockito.when(recommendationRequestRepository.findById(recRequestDto.getId()))
                .thenReturn(Optional.ofNullable(recRequestEntity));

        recommendationRequestService.rejectRequest(recRequestDto.getId(), rejectionDto);

        Assertions.assertEquals(rejectionReason, rejectionDto.getReason());
        verify(recommendationRequestRepository, times(1))
                .rejectRequest(recRequestDto.getId(), rejectionDto.getReason());
    }

    @Test
    public void testSaveSkillsInDb() {
        recommendationRequestService.saveSkillsInDb(recRequestDto);

        verify(skillRequestRepository, times(recRequestDto.getSkillsId().size()))
                .create(anyLong(), anyLong());
    }

}