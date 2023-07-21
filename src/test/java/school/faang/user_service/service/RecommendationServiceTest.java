package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.event.RecommendationMapper;
import school.faang.user_service.mapper.event.RecommendationMapperImpl;
import school.faang.user_service.mapper.event.SkillMapper;
import school.faang.user_service.mapper.event.SkillMapperImpl;
import school.faang.user_service.mapper.event.SkillOfferMapper;
import school.faang.user_service.mapper.event.SkillOfferMapperImpl;
import school.faang.user_service.mapper.event.UserSkillGuaranteeDtoMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendatorValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private RecommendatorValidator recommendatorValidator;
    @Mock
    private SkillOfferRepository skillOffersRepository;
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private UserSkillGuaranteeDtoMapper userSkillGuaranteeDtoMapper;
    @Spy
    private SkillOfferMapper skillOfferMapper = new SkillOfferMapperImpl();
    @Spy
    private RecommendationMapper recommendationMapper = new RecommendationMapperImpl(skillOfferMapper);
    @InjectMocks
    private RecommendationService recommendationService;


    @Test
    public void testUpdateRecommendation() {
        Skill skill = Skill.builder().id(1L).build();
        SkillOffer skillOffer = SkillOffer.builder().id(1L).skill(skill).recommendation(Recommendation.builder().id(1L).build()).build();
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).skillId(1L).recommendationId(1L).build();
        ArrayList<SkillOffer> list = new ArrayList<>();
        list.add(skillOffer);

        Recommendation recommendation = Recommendation.builder().id(1L).author(User.builder().id(1L).build())
                .receiver(User.builder().id(1L).build()).skillOffers(list).updatedAt(LocalDateTime.now().minusYears(1L)).build();
        RecommendationDto recommendationDto = recommendationMapper.toDto(recommendation);

        Mockito.when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendation));
        Mockito.when(recommendationRepository.update(recommendationDto.getAuthorId(), recommendationDto.getReceiverId(), recommendationDto.getContent())).thenReturn(recommendation);

        Mockito.when(skillOffersRepository.create(skillOfferDto.getSkillId(), 1L)).thenReturn(1L);
        Mockito.when(skillOffersRepository.findById(1L)).thenReturn(Optional.of(skillOffer));

        RecommendationDto actual = recommendationService.updateRecommendation(recommendationDto, 1L);
        RecommendationDto expected = recommendationMapper.toDto(recommendation);
        assertEquals(expected, actual);
    }

    @Test
    public void testDeleteRecommendation() {
        recommendationRepository.deleteById(1L);
        Mockito.verify(recommendationRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testGetAllUserRecommendation() {
        Page<Recommendation> page = new PageImpl<>(List.of(Recommendation.builder().skillOffers(List.of(SkillOffer.builder().id(1L).build())).build()));
        Mockito.when(recommendationRepository.findAllByReceiverId(1L, Pageable.unpaged())).thenReturn(page);

        List<RecommendationDto> recommendationDtos = recommendationService.getAllUserRecommendations(1L);
        assertEquals(1, recommendationDtos.size());
    }

    @Test
    public void testAllUserGivenRecommendations() {
        Page<Recommendation> page = new PageImpl<>(List.of(Recommendation.builder().skillOffers(List.of(SkillOffer.builder().id(1L).build())).build()));
        Mockito.when(recommendationRepository.findAllByAuthorId(1L, Pageable.unpaged())).thenReturn(page);

        List<RecommendationDto> recommendationDtos = recommendationService.getAllUserGivenRecommendations(1L);
        assertEquals(1, recommendationDtos.size());
    }
}