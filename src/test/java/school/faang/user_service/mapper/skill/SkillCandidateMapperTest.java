package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.mapper.SkillCandidateMapper;
import school.faang.user_service.mapper.SkillMapperImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SkillCandidateMapperTest {
    @Spy
    private SkillMapperImpl skillMapper = new SkillMapperImpl();

    @InjectMocks
    private SkillCandidateMapper skillCandidateMapper = new SkillCandidateMapper(skillMapper);

    @Test
    public void TestToCandidateDto(){
        Skill skillFirst = new Skill();
        Skill skillSecond = new Skill();
        skillFirst.setTitle("first");
        skillSecond.setTitle("second");
        SkillCandidateDto expectedFirst = new SkillCandidateDto(skillMapper.toDto(skillFirst), 2);
        SkillCandidateDto expectedSecond = new SkillCandidateDto(skillMapper.toDto(skillSecond), 2);
        List<SkillCandidateDto> expected = new ArrayList<>();
        expected.add(expectedFirst);
        expected.add(expectedSecond);
        expected = expected.stream()
                .sorted(Comparator.comparing(s -> s.getSkill().getTitle())).toList();
        List<Skill> skills = List.of(skillFirst, skillSecond, skillFirst, skillSecond);

        List<SkillCandidateDto> result = skillCandidateMapper.toCandidateDto(skills).stream()
                .sorted(Comparator.comparing(s -> s.getSkill().getTitle())).toList();

        assertEquals(expected, result);
    }
}
