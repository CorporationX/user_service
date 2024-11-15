package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filters.recommendation.request.RecommendationRequestFilter;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecommendationRequestServiceTest {
//
//    @Mock
//    private RecommendationRequestRepository recommendationRequestRepository;
//
//    @Mock
//    private RecommendationRepository recommendationRepository;
//
//    @Mock
//    private SkillRepository skillRepository;
//
//    @Mock
//    private SkillRequestRepository skillRequestRepository;
//
//    @Mock
//    private RecommendationRequestMapper requestMapper;
//
//    @Mock
//    private List<RecommendationRequestFilter> recRequestFilters;
//
//    @InjectMocks
//    private RecommendationRequestService recommendationRequestService;
//
//    private RecommendationRequestDto recRequestDto;
//    private RecommendationRequest recRequest;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        recRequestDto = new RecommendationRequestDto();
//        recRequestDto.setRequesterId(1L);
//        recRequestDto.setReceiverId(2L);
//        recRequestDto.setSkillsId(Collections.singletonList(1L));
//        recRequest = new RecommendationRequest();
//        recRequest.setId(1L);
//
//        when(requestMapper.toEntity(recRequestDto)).thenReturn(recRequest);
//        when(requestMapper.toDto(recRequest)).thenReturn(recRequestDto);
//    }
//
//    @Test
//    public void createTest() {
//        when(recommendationRequestRepository.checkTheUsersExistInDb(1L, 2L)).thenReturn(true);
//        when(skillRepository.countExisting(Collections.singletonList(1L))).thenReturn(1);
//        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
//
//        RecommendationRequestDto result = recommendationRequestService.create(recRequestDto);
//
//        verify(recommendationRepository, times(1)).create(1L, 2L, recRequest.getMessage());
//        verify(skillRequestRepository, times(1)).create(anyLong(), eq(1L));
//        assertEquals(recRequestDto, result);
//    }
}