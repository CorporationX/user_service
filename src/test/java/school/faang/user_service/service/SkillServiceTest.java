package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Spy
    private SkillMapperImpl skillMapper;
    @Mock
    private SkillOfferRepository offerRepository;
    @Mock
    private UserSkillGuaranteeRepository guaranteeRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private SkillService skillService;

    @Test
    public void testCreateNullValidation() {
        DataValidationException handledException =
                assertThrows(DataValidationException.class, () -> skillService.create(null));
        assertEquals("SkillDto from argument is null!", handledException.getMessage());
    }

    @Test
    public void testCreateNullTitleDto() {
        DataValidationException handledException
                = assertThrows(DataValidationException.class, () -> skillService.create(createSkillDto(null)));
        assertEquals("SkillDto has no name!", handledException.getMessage());
    }

    @Test
    public void testCreateEmptyTitleDto() {
        DataValidationException handledException =
                assertThrows(DataValidationException.class, () -> skillService.create(createSkillDto("   ")));
        assertEquals("SkillDto has no name!", handledException.getMessage());
    }

    @Test
    public void testCreateSaveNonExistentSkill() {
        SkillDto skill = createSkillDto("Skill");
        when(skillMapper.toDto(any())).thenReturn(skill);
        SkillDto skillResult = skillService.create(skill);

        verify(skillRepository, times(1)).save(any());
        assertEquals(skill, skillResult);
    }

    @Test
    public void testCreateWhenSkillAlreadyExists() {
        SkillDto newSkill = createSkillDto("Title");
        when(skillRepository.existsByTitle(newSkill.getTitle())).thenReturn(true);

        DataValidationException handledException =
                assertThrows(DataValidationException.class, () -> skillService.create(newSkill));
        assertEquals("Skill " + newSkill.getTitle() + " already exists!", handledException.getMessage());
    }

    private SkillDto createSkillDto(String title) {
        SkillDto skillDto = new SkillDto();
        skillDto.setTitle(title);
        skillDto.setId(11323L);
        return skillDto;
    }

    @Test
    public void testGetOfferedSkills() {
        Long userId = 777L;
        SkillDto dto = createSkillDto("Another Title");
        Skill mockedSkill = skillMapper.toEntity(dto);
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto(dto, 1L);

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(mockedSkill));

        List<SkillCandidateDto> offeredSkills = skillService.getOfferedSkills(userId);
        assertEquals(1, offeredSkills.size());
        assertEquals(skillCandidateDto, offeredSkills.get(0));
    }

    @Test
    public void testAcquireExistingSkillFromOffers() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.of(mock(Skill.class)));
        DataValidationException handledException =
                assertThrows(DataValidationException.class,
                        () -> skillService.acquireSkillFromOffers(1L, 1L));
        assertEquals("User already have this skill!", handledException.getMessage());
    }

    @Test
    public void testAcquireSkillFromOffersNotEnoughOffers() {
        when(offerRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(List.of());
        DataValidationException handledException =
                assertThrows(DataValidationException.class,
                        () -> skillService.acquireSkillFromOffers(1L, 1L));
        assertEquals("Not enough skill offers!", handledException.getMessage());
    }

    @Test
    public void testAcquireSkillFromOffersAssignSkill() {
        when(skillRepository.findUserSkill(anyLong(), anyLong())).thenReturn(Optional.empty());

        SkillDto randomDto = createSkillDto("RandomDto");
        Skill skill = skillMapper.toEntity(randomDto);
        UserSkillGuarantee mockSkillGuarantee = mock(UserSkillGuarantee.class);
        Recommendation recommendation = mock(Recommendation.class);
        when(recommendation.getAuthor()).thenReturn(mock(User.class));

        SkillOffer offer1 = mock(SkillOffer.class);
        when(offer1.getRecommendation()).thenReturn(recommendation);
        SkillOffer offer2 = mock(SkillOffer.class);
        when(offer2.getRecommendation()).thenReturn(recommendation);
        SkillOffer offer3 = mock(SkillOffer.class);
        when(offer3.getRecommendation()).thenReturn(recommendation);

        when(offerRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(List.of(offer1, offer2, offer3));
        when(offerRepository.findAllOffersOfSkill(anyLong(), anyLong())).thenReturn(List.of(offer1, offer2, offer3));
        when(skillMapper.toUserSkillGuarantee(userRepository, offer1, 1L)).thenReturn(mockSkillGuarantee);
        when(skillRepository.getReferenceById(1L)).thenReturn(skill);


        assertEquals(randomDto, skillService.acquireSkillFromOffers(1L, 1L));
        verify(skillRepository, times(1)).assignSkillToUser(anyLong(), anyLong());
        verify(guaranteeRepository, times(1)).save(mockSkillGuarantee);
    }

    @Test
    public void testGetUserSkills() {
        SkillDto anotherDto = createSkillDto("Another Dto");
        Skill skill = skillMapper.toEntity(anotherDto);
        when(skillRepository.findAllByUserId(anyLong())).thenReturn(List.of(skill));

        assertEquals(List.of(anotherDto), skillService.getUserSkills(1L));
    }
}
