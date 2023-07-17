package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationRequestServiceTest {

    @InjectMocks
    private RecommendationRequestService recommendationRequestService;

    @Mock
    private RecommendationRequestRepository recommendationRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper;

    private RecommendationRequestDto requestDto1;

    private RecommendationRequestDto requestDto2;

    @BeforeEach
    void setUp() {
        requestDto1 = RecommendationRequestDto.builder()
                .id(1L)
                .message("message")
                .status(RequestStatus.ACCEPTED)
                .skills(List.of(new SkillRequest(1L, new RecommendationRequest(), new Skill())))
                .requesterId(1L)
                .receiverId(1L)
                .createdAt(LocalDateTime.now().minusMonths(7))
                .build();
        requestDto2 = RecommendationRequestDto.builder()
                .id(1L)
                .build();
    }

    @Test
    void testValidationExistById() {
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationRequestDate() {
        requestDto1.setCreatedAt(LocalDateTime.now().minusMonths(7));
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
    }

    @Test
    void testValidationExistSkill() {
        requestDto1.setCreatedAt(LocalDateTime.now());
        assertThrows(DataValidationException.class, () -> recommendationRequestService.create(requestDto1));
        System.out.println(skillRepository.existsById(1L));
    }

    @Test
    void testGetRequests() {
        RecommendationRequest entity1 = recommendationRequestMapper.toEntity(requestDto1);
        RecommendationRequest entity2 = recommendationRequestMapper.toEntity(requestDto2);
        Mockito.when(recommendationRequestRepository.findAll())
                .thenReturn(List.of(entity1, entity2));

        RequestFilterDto requestFilterDto = RequestFilterDto.builder()
                .id(1)
                .build();

        List<RecommendationRequestDto> expected = List.of(requestDto1, requestDto2);

        List<RecommendationRequestDto> actual = recommendationRequestService.getRequests(requestFilterDto);

        assertEquals(expected, actual);
    }
}