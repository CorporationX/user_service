package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    private static final long SKILL_NEW_ID = 0L;
    private static final long SKILL_CREATED_ID = 1L;
    private static final String SKILL_TITLE = "Java";
    private static final long USER_ID = 1L;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillMapper skillMapper;

    @Mock
    private SkillCandidateMapper skillCandidateMapper;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @InjectMocks
    private SkillService skillService;

    SkillDto skillDto;
    SkillDto savedSkillDto;
    Skill savedSkill;

    @BeforeEach
    public void init() {
        skillDto = new SkillDto(SKILL_NEW_ID, SKILL_TITLE, null);
        savedSkillDto = new SkillDto(SKILL_CREATED_ID, SKILL_TITLE, null);
        savedSkill = Skill.builder()
                .id(SKILL_CREATED_ID)
                .title(SKILL_TITLE)
                .build();
    }

    @Test
    public void createSuccess() {
        Skill skill = Skill.builder()
                .id(SKILL_NEW_ID)
                .title(SKILL_TITLE)
                .build();

        when(skillRepository.save(skill)).thenReturn(savedSkill);
        when(skillMapper.dtoToSkill(skillDto)).thenReturn(skill);
        when(skillMapper.skillToDto(savedSkill)).thenReturn(savedSkillDto);

        SkillDto actualResult = skillService.create(skillDto);
        assertEquals(savedSkillDto, actualResult);
    }

    @Test
    public void createExistsSkillWithException() {
        String ERR_MSG = "Skill with title: '" + skillDto.getTitle() + "' already exists in DB";
        doThrow(new DataValidationException(ERR_MSG)).when(skillValidator).validateExistSkillByTitle(skillDto.getTitle());

        DataValidationException exception = assertThrows(DataValidationException.class, () -> skillService.create(skillDto));

        assertThat(exception.getMessage()).isEqualTo(ERR_MSG);
    }

    @Test
    public void getAllSkillsByUserId() {
        List<Skill> skills = List.of(savedSkill);
        List<SkillDto> skillDtoList = List.of(savedSkillDto);

        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(skills);
        when(skillMapper.map(skills)).thenReturn(skillDtoList);

        List<SkillDto> actualResult = skillService.getUserSkills(USER_ID);

        assertEquals(1, actualResult.size());
        assertEquals(savedSkillDto, actualResult.get(0));

    }

    @Test
    public void getAllSkillsByUserIdWhenNoSkills() {
        List<SkillDto> actualResult = skillService.getUserSkills(USER_ID);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void getOfferedSkills() {
        List<Skill> skills = List.of(savedSkill);
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(savedSkillDto, 1);

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(skills);
        when(skillCandidateMapper.skillToCandidateDto(savedSkill, 1)).thenReturn(skillCandidateDto);

        List<SkillCandidateDto> actualResult = skillService.getOfferedSkills(USER_ID);

        assertEquals(1, actualResult.size());
        assertEquals(skillCandidateDto, actualResult.get(0));
    }

    @Test
    public void getAllOfferedSkillsByUserIdWhenNoSkills() {
        List<SkillCandidateDto> actualResult = skillService.getOfferedSkills(USER_ID);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    public void acquireSkillFromOffersWhenSkillExists() {
        String ERR_MSG = "User " + USER_ID + " already has skill with ID: " + SKILL_CREATED_ID;
        doThrow(new DataValidationException(ERR_MSG)).when(skillValidator).validateSkillPresent(SKILL_CREATED_ID, USER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID));
        assertThat(exception.getMessage()).isEqualTo(ERR_MSG);
    }

    @Test
    public void acquireSkillFromOffersWhenSkillOffersToSmall() {
        SkillOffer offer1 = new SkillOffer();
        SkillOffer offer2 = new SkillOffer();
        List<SkillOffer> skillOfferList = List.of(offer1, offer2);
        String OFFER_ERR_MSG = "Skill with ID: " + SKILL_CREATED_ID + " hasn't enough offers for user with ID: " + USER_ID;

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(skillOfferList);
        doThrow(new DataValidationException(OFFER_ERR_MSG)).when(skillValidator).validateMinSkillOffers(skillOfferList.size(), SKILL_CREATED_ID, USER_ID);

        DataValidationException exception = assertThrows(DataValidationException.class, () -> skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID));
        assertThat(exception.getMessage()).isEqualTo(OFFER_ERR_MSG);
    }

    @Test
    public void acquireSkillFromOffersWhenSkillNoExists() {
        User receiver = User.builder().id(USER_ID).build();
        User author1 = new User();
        User author2 = new User();
        User author3 = new User();
        SkillOffer offer1 = new SkillOffer(1, savedSkill, Recommendation.builder().author(author1).receiver(receiver).build());
        SkillOffer offer2 = new SkillOffer(2, savedSkill, Recommendation.builder().author(author2).receiver(receiver).build());
        SkillOffer offer3 = new SkillOffer(3, savedSkill, Recommendation.builder().author(author3).receiver(receiver).build());
        List<SkillOffer> skillOfferList = List.of(offer1, offer2, offer3);

        when(skillOfferRepository.findAllOffersOfSkill(SKILL_CREATED_ID, USER_ID)).thenReturn(skillOfferList);
        when(skillMapper.skillToDto(savedSkill)).thenReturn(savedSkillDto);

        SkillDto actualResult = skillService.acquireSkillFromOffers(SKILL_CREATED_ID, USER_ID);
        assertEquals(savedSkillDto, actualResult);
    }
}