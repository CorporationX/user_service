package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private RecommendationMapper recommendationMapper;
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private SkillOfferValidator skillOfferValidator;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper;


    @Test
    public void testCreateRecommendation() {
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setId(1L);
        recommendationDto.setAuthorId(2L);
        recommendationDto.setReceiverId(3L);
        recommendationDto.setContent("content");
        recommendationDto.setSkillOffers(new ArrayList<>());

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        User author = new User();
        author.setId(2L);
        recommendationEntity.setAuthor(author);
        User receiver = new User();
        receiver.setId(3L);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent("content");
        recommendationEntity.setSkillOffers(new ArrayList<>());

        when(recommendationMapper.toEntity(any(RecommendationDto.class))).thenReturn(recommendationEntity);
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationRepository, times(1)).save(any(Recommendation.class));

        assertDoesNotThrow(() -> recommendationService.create(recommendationDto));
        assertEquals(recommendationDto.getId(), result.getId());
        assertEquals(recommendationDto.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffers(), result.getSkillOffers());
    }
}
