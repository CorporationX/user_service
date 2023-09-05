package school.faang.user_service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
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
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillCandidateMapperImpl;
import school.faang.user_service.mapper.skill.SkillMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;
    @Spy
    private SkillMapperImpl skillMapper;
    @Spy
    private SkillCandidateMapperImpl skillCandidateMapper;

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserService userService;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    SkillDto skillDto;
    Skill skill1;
    Skill skill2;
    List<Skill> list1;
    List<Skill> list2;
    SkillOffer skillOffer;
    Recommendation recommendation = new Recommendation();
    User author = new User();
    User receiver = new User();



    @BeforeEach
    public void setUp() {
        skillDto = SkillDto.builder().id(1L).title("flexibility").build();
        skill1 = new Skill(1L, "test", new ArrayList<User>(), new ArrayList<UserSkillGuarantee>(), new ArrayList<Event>(),
                new ArrayList<Goal>(), LocalDateTime.now().minusDays(1), LocalDateTime.now());
        skill2 = new Skill(1L, "test", new ArrayList<User>(), new ArrayList<UserSkillGuarantee>(), new ArrayList<Event>(),
                new ArrayList<Goal>(), LocalDateTime.now().minusDays(1), LocalDateTime.now());
        list1 = new ArrayList<>(List.of(skill1));
        list2 = new ArrayList<>(List.of(skill1, skill2));
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        skillOffer = new SkillOffer(1L, skill1, recommendation);

    }

    @Test
    void testCreateExistingSkillThrowsException() {
        when(skillRepository.existsByTitle(anyString()))
                .thenReturn(true);
        assertTrue(skillRepository.existsByTitle(anyString()));
        assertThrows(DataValidationException.class,
                () -> skillService.create(skillDto));
    }

    @Test
    void testCreateSkill() {
        skillService.create(skillDto);
        verify(skillRepository, times(1)).save(any());
    }

    @Test
    void testCallMethodFindByIdAndFindAllByUserId() {
        skillService.getUserSkills(1L);
        verify(userService, times(1)).getUser(1L);
        verify(skillRepository, times(1)).findAllByUserId(1L);
    }

    @Test
    void testMapperFromDTOtoEntity() {
        assertInstanceOf(Skill.class, skillMapper.toEntity(skillDto));
        Skill skillResult = skillMapper.toEntity(skillDto);
        assertEquals(skillResult.getTitle(), skillDto.getTitle());
        assertEquals(skillResult.getId(), skillDto.getId());
    }

    @Test
    void testCallMethodFindSkillsOfferedToUser() {
        skillService.getOfferedSkills(1L);
        verify(skillRepository, times(1)).findSkillsOfferedToUser(1L);
    }

    @Test
    void testMapperFromEmptySkillList() {
        when(skillRepository.findSkillsOfferedToUser(anyLong())).thenReturn(new ArrayList<Skill>());
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);
        assertEquals(0, result.size());
    }

    @Test
    void testMapperFromOneElementSkillList() {
        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(list1);
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);
        assertEquals(1, result.size());
    }

/*    @Test
    void testMapperFromManyElementsSkillList() {
        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(list2);
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);
        assertEquals(2, result.size());
    }*/

    @Test
    void testAcquireSkillFromOffersThrowsException() {
        when(skillRepository.findUserSkill(anyLong(), anyLong()))
                .thenReturn(Optional.of(skill1));
        assertThrows(DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1L, 1L));
    }

/*    @Test
    void testAcquireSkillFromOffers() {
        when(skillRepository.findUserSkill(1L, 1L))
                .thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(1L, 1L))
                .thenReturn(List.of(skillOffer, skillOffer, skillOffer, skillOffer));
        skillService.acquireSkillFromOffers(1L, 1L);
        verify(skillRepository, times(1)).assignSkillToUser(1L, 1L);
    }*/

}