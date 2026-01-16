package school.faang.user_service.service.skill;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapperImpl;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;


@ExtendWith(MockitoExtension.class)
public class SkillServiceImplTest {
    @Value("${skill.offers.min.count}")
    private int minCountOffers;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @InjectMocks
    private SkillServiceImpl skillService;
    @Spy
    private SkillMapperImpl skillMapper;
    @Captor
    private ArgumentCaptor<Skill> skillArgumentCaptor;
    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Test
    public void testCreateWithExistingTitle() {
        CreateSkillDto dto = prepareData(true);

        assertThrows(ForbiddenException.class, () -> skillService.create(dto));
    }

    @Test
    public void testCreateSaveSkill() {
        CreateSkillDto dto = prepareData(false);

        skillService.create(dto);
        verify(skillRepository, times(1)).save(skillArgumentCaptor.capture());
        Skill skill = skillArgumentCaptor.getValue();
        assertEquals(dto.getTitle(), skill.getTitle());
    }

    private CreateSkillDto prepareData(boolean existsByTitle) {
        CreateSkillDto dto = new CreateSkillDto("title");
        when(skillRepository.existsByTitle(dto.getTitle())).thenReturn(existsByTitle);
        return dto;
    }

    @Test
    public void testGetByUserIdCallMethod() {
        Long userId = 1L;
        Skill firstSkill = Skill.builder()
                .id(1L)
                .title("firstSkill")
                .build();
        Skill secondSkill = Skill.builder()
                .id(2L)
                .title("secondSkill")
                .build();

        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(firstSkill, secondSkill));

        skillService.getByUserId(userId);

        verify(skillRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void testGetOfferedSkillsCallFindSkillsOfferedToUser() {
        Long userId = 1L;
        Skill firstSkill = Skill.builder()
                .id(1L)
                .title("firstSkill")
                .build();
        Skill secondSkill = Skill.builder()
                .id(2L)
                .title("secondSkill")
                .build();

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(firstSkill, secondSkill));

        skillService.getOfferedSkills(userId);

        verify(skillRepository, times(1)).findSkillsOfferedToUser(userId);
    }

    @Test
    public void testGetOfferedSkillsCallCountAllOffersOfSkill() {
        Long userId = 1L;
        Skill firstSkill = Skill.builder()
                .id(1L)
                .title("firstSkill")
                .build();
        Skill secondSkill = Skill.builder()
                .id(2L)
                .title("secondSkill")
                .build();

        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(List.of(firstSkill, secondSkill));
        when(skillOfferRepository.countAllOffersOfSkill(anyLong(), anyLong())).thenReturn(4);

        skillService.getOfferedSkills(userId);

        verify(skillOfferRepository, times(2)).countAllOffersOfSkill(longArgumentCaptor.capture(), eq(userId));
    }

    @Test
    public void testAcquireSkillFromOffersWithLessThanMinCountOffers() {
        long skillId = 1L;
        long userId = 1L;
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(minCountOffers - 1);

        assertThrows(ForbiddenException.class, () -> skillService.acquireSkillFromOffers(skillId, userId));
    }

    @Test
    public void testAcquireSkillFromOffersWithMoreThanMinCountOffers() {
        long skillId = 1L;
        long userId = 1L;
        when(skillOfferRepository.countAllOffersOfSkill(skillId, userId)).thenReturn(minCountOffers + 1);

        skillService.acquireSkillFromOffers(skillId, userId);

        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
    }
}