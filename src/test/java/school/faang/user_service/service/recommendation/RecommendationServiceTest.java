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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    public void testCreate() {
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

        RecommendationDto updatedRecommendationDto = recommendationService.create(recommendationDto);

        verify(recommendationValidator).validateLastUpdate(recommendationDto);
        verify(skillOfferValidator).validateSkillsListNotEmptyOrNull(recommendationDto.getSkillOffers());
        verify(skillOfferValidator).validateSkillsAreInRepository(recommendationDto.getSkillOffers());
        verify(recommendationRepository, times(1)).save(any(Recommendation.class));
        assertEquals(updatedRecommendationDto, recommendationDto);
    }

    @Test
    public void testsUpdate() {
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

        RecommendationDto updatedRecommendationDto = recommendationService.update(recommendationDto);

        verify(recommendationValidator).validateLastUpdate(recommendationDto);
        verify(skillOfferValidator).validateSkillsListNotEmptyOrNull(recommendationDto.getSkillOffers());
        verify(skillOfferValidator).validateSkillsAreInRepository(recommendationDto.getSkillOffers());
        verify(recommendationRepository).deleteById(1L);
        verify(recommendationRepository).save(recommendationEntity);
        assertEquals(updatedRecommendationDto, recommendationDto);
    }

    @Test
    public void testDelete() {
        long recommendationId = 1L;

        doNothing().when(recommendationRepository).deleteById(anyLong());
        recommendationService.delete(recommendationId);
        verify(recommendationRepository).deleteById(recommendationId);
    }

    @Test
    public void testGetAllUserRecommendations() {
        long receiverId = 3L;
        List<Recommendation> receiverRecommendations = new ArrayList<>();
        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        receiverRecommendations.add(recommendation1);
        receiverRecommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtos = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        recommendationDtos.add(recommendationDto1);
        recommendationDtos.add(recommendationDto2);

        when(recommendationRepository.findAllByReceiverId(receiverId)).thenReturn(receiverRecommendations);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationRepository, times(1)).findAllByReceiverId(receiverId);
        verify(recommendationMapper, times(1)).toDto(recommendation1);
        verify(recommendationMapper, times(1)).toDto(recommendation2);
        assertEquals(recommendationDtos.size(), result.size());
        assertEquals(recommendationDtos, result);
    }


    @Test
    public void testGetAllGivenRecommendations() {
        long receiverId = 3L;

        List<Recommendation> receiverRecommendations = new ArrayList<>();
        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(1L);
        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(2L);
        receiverRecommendations.add(recommendation1);
        receiverRecommendations.add(recommendation2);

        List<RecommendationDto> recommendationDtos = new ArrayList<>();
        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(1L);
        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(2L);
        recommendationDtos.add(recommendationDto1);
        recommendationDtos.add(recommendationDto2);

        when(recommendationRepository.findAllByReceiverId(receiverId)).thenReturn(receiverRecommendations);
        when(recommendationMapper.toDto(recommendation1)).thenReturn(recommendationDto1);
        when(recommendationMapper.toDto(recommendation2)).thenReturn(recommendationDto2);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationRepository, times(1)).findAllByReceiverId(receiverId);
        verify(recommendationMapper, times(1)).toDto(recommendation1);
        verify(recommendationMapper, times(1)).toDto(recommendation2);
        assertEquals(recommendationDtos.size(), result.size());
        assertEquals(recommendationDtos, result);
    }
}
