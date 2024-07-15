package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Nested
@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillValidator skillValidator;

    @Mock
    private SkillOfferRepository offerRepository;

    @Mock
    private SkillMapper skillMapper;

    @Captor
    private ArgumentCaptor<Skill> skillCaptor;

    private SkillCandidateDto skillCandidateDto;
    private SkillDto skillDto;
    private Skill skill;
    private User user;
    private long userId;
    private long skillId;
    private SkillOffer skillOffer;
    private static final int MIN_SKILL_OFFERS = 3;

    @InjectMocks
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        userId = 1L;
        skillId = 1L;
        skillDto = new SkillDto();
        skillDto.setId(skillId);
        skillDto.setTitle("title");
        skillDto.setUserIds(List.of(1L, 2L));
        skill = new Skill();
        skill.setId(skillId);
        skill.setTitle("title");
        skill.setUsers(List.of(new User(), new User()));
        user = new User();
        user.setId(userId);
        skillOffer = new SkillOffer();
        skillOffer.setId(skillId);
        skillOffer.setSkill(skill);
    }

    @Test
    @DisplayName("testCreate")
    void testCreate() {
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(userRepository.findAllById(skillDto.userIds))
                .thenReturn(List.of(new User(), new User()));
        when(skillRepository.save(any())).thenReturn(skill);
        when(skillMapper.toEntity(skillDto)).thenReturn(skill);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto skillDtoResult = skillService.create(skillDto);

        verify(skillMapper).toEntity(skillDto);
        verify(skillRepository).save(skillCaptor.capture());
        verify(skillMapper).toDto(skill);
        Skill skillCaptorValue = skillCaptor.getValue();
        assertEquals(skillDto.getTitle(), skillCaptorValue.getTitle());
        assertEquals(skillDto.getTitle(), skillDtoResult.getTitle());
        assertEquals(skillCaptorValue, skill);
    }

    @Test
    @DisplayName("testGetUserSkills")
    void testGetUserSkills() {
        when(skillRepository.findAllByUserId(userId)).thenReturn(getSkillList());
        when(skillMapper.toDtoSkillEntity(getSkillList())).thenReturn(getSkillDtoList());

        List<SkillDto> userSkillsResult = skillService.getUserSkills(userId);

        verify(skillRepository).findAllByUserId(userId);
        verify(skillMapper).toDtoSkillEntity(getSkillList());
        assertEquals(userSkillsResult, getSkillDtoList());
    }

    @Test
    @DisplayName("testGetOfferedSkills")
    void testGetOfferedSkills() {
        skill.setUsers(List.of(user));
        SkillCandidateDto skillCandidateDto = new SkillCandidateDto();
        when(skillRepository.findSkillsOfferedToUser(userId))
                .thenReturn(List.of(skill));
        when(skillMapper.toCandidateDto(List.of(skill)))
                .thenReturn(List.of(skillCandidateDto));

        List<SkillCandidateDto> offeredSkillsResult = skillService.getOfferedSkills(userId);

        verify(skillRepository).findSkillsOfferedToUser(userId);
        verify(skillMapper).toCandidateDto(List.of(skill));
        assertEquals(offeredSkillsResult, List.of(skillCandidateDto));

    }

    @Test
    void testAcquireSkillFromOffers() {
        when(skillRepository.findUserSkill(skillId, userId))
                .thenReturn(Optional.empty());
        when(offerRepository.findAllOffersOfSkill(skillId, userId))
                .thenReturn(List.of(skillOffer));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        SkillDto skillDtoResult = skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository).findUserSkill(skillId, userId);
        verify(offerRepository).findAllOffersOfSkill(skillId, userId);
        verify(skillMapper).toDto(skill);
        assertNotNull(skillDtoResult);
        assertEquals(skillDtoResult, skillDto);
    }

    private List<SkillDto> getSkillDtoList() {
        SkillDto firstSkillDto = new SkillDto();
        firstSkillDto.setTitle("title");
        firstSkillDto.setUserIds(List.of(userId));
        SkillDto secondSkillDto = new SkillDto();
        secondSkillDto.setTitle("title1");
        secondSkillDto.setUserIds(List.of(userId));
        return List.of(firstSkillDto, secondSkillDto);
    }

    private List<Skill> getSkillList() {
        Skill firstSkill = new Skill();
        firstSkill.setTitle("title");
        firstSkill.setUsers(List.of(user));
        Skill secondSkill = new Skill();
        secondSkill.setTitle("title1");
        secondSkill.setUsers(List.of(user));
        return List.of(firstSkill, secondSkill);
    }

}