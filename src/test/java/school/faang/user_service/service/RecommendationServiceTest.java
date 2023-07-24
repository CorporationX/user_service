package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {
    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void validateSkillsNotExist() {
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).build();

        skills.add(skillOfferDto);

        when(skillRepository.existsAllById(anyList())).thenReturn(false);
        recommendationDto.setSkillOffers(skills);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
        assertEquals("list of skills contains not valid skills, please, check this", ex.getMessage());

    }

    @Test
    void createRecommendationTest() {

        User user = User.builder().id(1L).skills(List.of(new Skill())).build();
        Skill skill = Skill.builder()
                .guarantees(new ArrayList<>(List.of(new UserSkillGuarantee()))).build();
        List<Skill> userSkills = new ArrayList<>();
        userSkills.add(skill);
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(skills);

        Recommendation recommendation = new Recommendation();
        recommendation.setAuthor(user);
        recommendation.setReceiver(user);
        recommendation.setSkillOffers(List.of(new SkillOffer()));

        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).skillId(1L).build();

        skills.add(skillOfferDto);

        when(skillRepository.existsAllById(anyList())).thenReturn(true);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(user));
        when(skillRepository.findAllById(anyList())).thenReturn(userSkills);
        when(userSkillGuaranteeRepository.saveAll(anyList())).thenReturn(null);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        RecommendationDto result = recommendationService.create(recommendationDto);
        System.out.println(result);


    }
}