package school.faang.user_service.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mappers.SkillMapper;
import school.faang.user_service.repository.SkillRepository;


@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private SkillMapper skillMapper;


    @Test
    void testCreate() {
        SkillDto skillDto= new SkillDto(1L, "privet");
        skillService.create(skillDto);
        Mockito.verify(skillRepository, Mockito.times(1)).save(skillMapper.toEntity(skillDto));
    }

    @Test
    void testCreateExistByTitle() {
        SkillDto skillDto = new SkillDto(1L, "privet");
        Mockito.when(skillRepository.existsByTitle(skillDto.getTitle()))
                .thenReturn(true);

        Assert.assertThrows(DataValidationException.class, () -> skillService.create(skillDto));
    }

    @Test
    void testGetUserSkills() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(4L);

        skillService.getUserSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(skillDto.getId());
    }

    @Test
    void testGetOfferedSkills() {
        SkillDto skillDto = new SkillDto();
        skillDto.setId(4L);

        skillService.getOfferedSkills(skillDto.getId());
        Mockito.verify(skillRepository, Mockito.times(1))
                .findAllByUserId(skillDto.getId());
    }
}