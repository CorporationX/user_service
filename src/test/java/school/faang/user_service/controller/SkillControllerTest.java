package school.faang.user_service.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import reactor.core.publisher.Mono;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.SkillService;

import javax.swing.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class SkillControllerTest {
    @InjectMocks
    private SkillController skillController;
    @Mock
    private SkillService skillService;

    @Test
    void testCreate_DataValidationException() {
        SkillDto skillDto = SkillDto.builder().title(null).build();
        Assertions.assertThrows(DataValidationException.class, () -> skillController.create(skillDto));
  }

    @Test
    void testCreate_Create() {
        SkillDto skillDto = SkillDto.builder().title("test").build();
        skillController.create(skillDto);
        verify(skillService).create(skillDto);
    }

    @Test
    void testValidateSkill_Valid() {
        SkillDto skillDto1 = SkillDto.builder().title("test").build();
        SkillDto skillDto2 = SkillDto.builder().build();
        SkillDto skillDto3 = SkillDto.builder().title(" ").build();
        SkillDto skillDto4 = SkillDto.builder().title("").build();
        boolean valid1 = skillController.validateSkill(skillDto1);
        boolean valid2 = skillController.validateSkill(skillDto2);
        boolean valid3 = skillController.validateSkill(skillDto3);
        boolean valid4 = skillController.validateSkill(skillDto4);
        assertEquals(valid1, true);
        assertEquals(valid2, false);
        assertEquals(valid3, false);
        assertEquals(valid4, false);
    }
}