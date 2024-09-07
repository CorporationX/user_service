package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.Skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.skill.SkillValidator;
import javax.xml.bind.ValidationException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class SkillServiceTest {
    @InjectMocks
    private  SkillService skillService;

    @Mock
    private  SkillRepository skillRepository;
    @Spy
    private  SkillMapper skillMapper;
    @Mock
    private  SkillOfferRepository skillOfferRepository;
    @Mock
    private  SkillCandidateMapper skillCandidateMapper;
    @Mock
    private  SkillValidator skillValidator;

   @Test
    public void createWithBlankTitle() {
       SkillDto dto = new SkillDto("title",1L);

   }
}
