package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.mapper.RecommendationMapperImpl;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    private final Long ID = 1L;
    private final Long AUTHOR_ID = 2L;
    private final Long RECEIVER_ID = 3L;
    private final String CONTENT = "Content";
    private final List<SkillOfferDto> SKILL_OFFERS_DTO_LIST = List.of(
            new SkillOfferDto(11L, 111L),
            new SkillOfferDto(12L, 112L),
            new SkillOfferDto(13L, 113L),
            new SkillOfferDto(14L, 114L)
    );
    private final LocalDateTime CREATED_AT = LocalDateTime.of(2024, 11, 4, 19, 16);
    private final LocalDateTime CREATED_AT_MORE_SIX_MONTH = LocalDateTime.of(2024, 4, 4, 19, 16);

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferService skillOfferService;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Spy
    private RecommendationMapperImpl recommendationMapper;

    @InjectMocks
    private RecommendationService recommendationService;

    @Captor
    private ArgumentCaptor<List<Long>> captor;

    @Test
    public void testCreateDateValidator() {
        RecommendationDto recommendationDto = new RecommendationDto(ID, AUTHOR_ID, RECEIVER_ID,
                CONTENT, SKILL_OFFERS_DTO_LIST, CREATED_AT);

        Assert.assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    public void testCreateSkillOffersValidator() {
        RecommendationDto recommendationDto = new RecommendationDto(ID, AUTHOR_ID, RECEIVER_ID,
                CONTENT, SKILL_OFFERS_DTO_LIST, CREATED_AT_MORE_SIX_MONTH);
        List<Long> skillIds = recommendationDto.skillOffers().stream().map(SkillOfferDto::skillId).toList();
        when(skillRepository.countExisting(skillIds)).thenReturn(skillIds.size() - 1);

        Assert.assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
        verify(skillRepository, times(1)).countExisting(skillIds);
    }

    @Test
    public void testCreate() {
        Recommendation recommendation = createRecommendations().get(0);
        RecommendationDto recommendationDto = new RecommendationDto(ID, AUTHOR_ID, RECEIVER_ID,
                CONTENT, SKILL_OFFERS_DTO_LIST, CREATED_AT_MORE_SIX_MONTH);
        List<Long> skillIds = recommendationDto.skillOffers().stream().map(SkillOfferDto::skillId).toList();
        when(skillRepository.countExisting(skillIds)).thenReturn(skillIds.size());
        when(recommendationRepository.create(recommendationDto.authorId(), recommendationDto.receiverId(),
                recommendationDto.content()))
                .thenReturn(ID);
        when(recommendationRepository.findById(ID)).thenReturn(
                Optional.ofNullable(recommendation));

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(skillRepository, times(1)).countExisting(skillIds);
        verify(recommendationRepository, times(1)).findById(ID);
        assertEquals(ID, result.id());
    }

    @Test
    public void testUpdate() {
        RecommendationDto recommendationDto = new RecommendationDto(ID, AUTHOR_ID, RECEIVER_ID,
                CONTENT, SKILL_OFFERS_DTO_LIST, CREATED_AT_MORE_SIX_MONTH);
        when(skillRepository.countExisting(captor.capture())).thenReturn(SKILL_OFFERS_DTO_LIST.size());

        recommendationService.update(recommendationDto);

        verify(skillOfferService, times(1)).deleteAllSkillOffers(recommendationDto.id());
        verify(skillOfferService, times(1)).saveSkillOffers(
                recommendationDto.skillOffers(),
                recommendationDto.id()
        );
        verify(recommendationRepository, times(1)).update(
                recommendationDto.authorId(),
                recommendationDto.receiverId(),
                recommendationDto.content()
        );
    }

    @Test
    public void testDeleteRecommendation() {
        recommendationService.delete(ID);

        verify(recommendationRepository, times(1)).deleteById(ID);
    }

    @Test
    public void testGetAllUserRecommendations() {
        Page<Recommendation> allGivenRecommendation = new PageImpl<>(createRecommendations());
        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, Pageable.unpaged()))
                .thenReturn(allGivenRecommendation);

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(RECEIVER_ID);

        verify(recommendationRepository, times(1)).findAllByReceiverId(RECEIVER_ID, Pageable.unpaged());
        assertEquals(777L, result.get(0).authorId());
        assertEquals(33L, result.get(1).skillOffers().get(0).skillId());
    }

    @Test
    public void testGetAllGivenRecommendations() {
        Page<Recommendation> allGivenRecommendation = new PageImpl<>(createRecommendations());
        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, Pageable.unpaged()))
                .thenReturn(allGivenRecommendation);

        recommendationService.getAllGivenRecommendations(AUTHOR_ID);

        verify(recommendationRepository, times(1)).findAllByAuthorId(AUTHOR_ID, Pageable.unpaged());
    }

    private List<Recommendation> createRecommendations() {
        User author = new User();
        author.setId(777L);
        User receiver = new User();
        receiver.setId(999L);

        Skill javaSkill = new Skill();
        javaSkill.setId(22L);
        Skill gitSkill = new Skill();
        gitSkill.setId(33L);

        SkillOffer javaSkillOffer = new SkillOffer();
        javaSkillOffer.setId(44L);
        javaSkillOffer.setSkill(javaSkill);
        SkillOffer gitSkillOffer = new SkillOffer();
        gitSkillOffer.setId(55L);
        gitSkillOffer.setSkill(gitSkill);

        Recommendation firstRecommendation = new Recommendation();
        firstRecommendation.setId(1L);
        firstRecommendation.setAuthor(author);
        firstRecommendation.setReceiver(receiver);
        firstRecommendation.setSkillOffers(List.of(javaSkillOffer));
        firstRecommendation.setContent("first recommendation");
        firstRecommendation.setCreatedAt(LocalDateTime.now());
        Recommendation secondRecommendation = new Recommendation();
        secondRecommendation.setAuthor(author);
        secondRecommendation.setReceiver(receiver);
        secondRecommendation.setSkillOffers(List.of(gitSkillOffer));
        secondRecommendation.setContent("second recommendation");
        secondRecommendation.setCreatedAt(LocalDateTime.now());

        return List.of(firstRecommendation, secondRecommendation);
    }
}
