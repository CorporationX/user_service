package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillCandidateMapperTest {

    SkillCandidateMapper skillCandidateMapper = Mappers.getMapper(SkillCandidateMapper.class);

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final long THIRD_ID = 3L;
    private static final String FIRST_TITLE = "Бегит";
    private static final String SECOND_TITLE = "Анжуманя";
    private static final String THIRD_TITLE = "Прес качат";

    @Test
    void testToDtoList (){
        List<SkillDto> skillsDto = new ArrayList<>();
        SkillDto begit = new SkillDto(FIRST_ID, FIRST_TITLE);
        skillsDto.add(begit);
        skillsDto.add(begit);
        skillsDto.add(new SkillDto(SECOND_ID, SECOND_TITLE));
        skillsDto.add(new SkillDto(THIRD_ID, THIRD_TITLE));

        List<SkillCandidateDto> result = skillCandidateMapper.toDtoList(skillsDto);

        assertEquals(SECOND_ID, result.get(0).getOffersAmount());
        assertEquals(THIRD_ID, result.size());
        assertEquals(THIRD_TITLE, result.get(2).getSkill().getTitle());
    }

}