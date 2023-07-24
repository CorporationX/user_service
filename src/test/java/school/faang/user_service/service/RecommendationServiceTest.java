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
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    //    @Spy
//    private SkillValidator skillValidator = new SkillValidator(skillRepository);
    @Spy
    private RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;
    @InjectMocks
    private RecommendationService recommendationService;

    @Test
    void validateNotUniqueSkillIds() {
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).build();

        skills.add(skillOfferDto);
        skills.add(skillOfferDto);

        recommendationDto.setSkillOffers(skills);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
        assertEquals("list of skills contains not unique skills, please, check this", ex.getMessage());

    }

    @Test
    void validateNUniqueSkillIds() {
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).build();

        skills.add(skillOfferDto);

        when(skillRepository.countExisting(anyList())).thenReturn(2);
        recommendationDto.setSkillOffers(skills);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
        assertEquals("list of skills contains not valid skills, please, check this", ex.getMessage());

    }

    @Test
    void createRecommendationTest() {

        User user = User.builder().id(1L).build();
        Skill skill = Skill.builder()
                .guarantees(new ArrayList<>(List.of(new UserSkillGuarantee()))).build();
        List<Skill> userSkills = new ArrayList<>();
        userSkills.add(skill);
        List<SkillOfferDto> skills = new ArrayList<>();
        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(1L);
        recommendationDto.setReceiverId(2L);
        recommendationDto.setSkillOffers(skills);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        SkillOfferDto skillOfferDto = SkillOfferDto.builder().id(1L).build();

        skills.add(skillOfferDto);
        when(skillRepository.countExisting(anyList())).thenReturn(1);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user))
                .thenReturn(Optional.of(user));
        when(skillRepository.findAllByUserId(1L)).thenReturn(userSkills);
        when(skillRepository.findById(1L)).thenReturn(Optional.of(skill));
when(recommendationRepository.save(recommendation)).thenReturn(recommendation);
RecommendationDto result = recommendationService.create(recommendationDto);
        System.out.println(result);


    }
}