package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillOfferValidator;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private RecommendationValidator recommendationValidator;

    @Mock
    private RecommendationMapper recommendationMapper;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillOfferValidator skillOfferValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @InjectMocks
    private RecommendationService recommendationService;


    private RecommendationDto testRecommendationDto;
    private Recommendation testRecommendation;

    @BeforeEach
    public void setUp() {
        testRecommendationDto = new RecommendationDto();
        testRecommendationDto.setAuthorId(1L);
        testRecommendationDto.setReceiverId(2L);

        // Create a test Recommendation entity
        testRecommendation = new Recommendation();
        testRecommendation.setId(1L);
        User author = new User();
        author.setId(1L);
        testRecommendation.setAuthor(author);
        User receiver = new User();
        receiver.setId(2L);
        testRecommendation.setReceiver(receiver);
    }

    @Test
    public void testCreateRecommendation_Success() {
        // Arrange
        when(recommendationMapper.toEntity(testRecommendationDto)).thenReturn(testRecommendation);
        when(recommendationRepository.save(testRecommendation)).thenReturn(testRecommendation);
        when(recommendationMapper.toDto(testRecommendation)).thenReturn(testRecommendationDto);

        // Act
        RecommendationDto createdRecommendation = recommendationService.create(testRecommendationDto);

        // Assert
        assertNotNull(createdRecommendation);
        assertEquals(testRecommendationDto.getAuthorId(), createdRecommendation.getAuthorId());
        assertEquals(testRecommendationDto.getReceiverId(), createdRecommendation.getReceiverId());
    }
}
