package school.faang.user_service.mapper.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.mapper.skill.SkillCandidateMapper;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkillCandidateMapperTest {

    SkillCandidateMapper skillCandidateMapper = Mappers.getMapper(SkillCandidateMapper.class);

    private static final long FIRST_ID = 1L;
    private static final long SECOND_ID = 2L;
    private static final long THIRD_ID = 3L;
    private static final String FIRST_TITLE = "squating";
    private static final String SECOND_TITLE = "lifting";
    private static final String THIRD_TITLE = "spotting";

    private List<SkillDto> skillDtos;

    @BeforeEach
    public void init() {
        SkillDto firstDto = SkillDto.builder()
                .id(FIRST_ID)
                .title(FIRST_TITLE)
                .build();
        SkillDto secondDto = SkillDto.builder()
                .id(SECOND_ID)
                .title(SECOND_TITLE)
                .build();
        SkillDto thirdDto = SkillDto.builder()
                .id(THIRD_ID)
                .title(THIRD_TITLE)
                .build();
        skillDtos = List.of(firstDto, firstDto, secondDto, thirdDto);
    }
    @Test
    @DisplayName("Testing mapping")
    void whenSkillDtoListMappedToSkillCandidateDtoListThenSuccess() {
        List<SkillCandidateDto> result = skillCandidateMapper.toSkillCandidateDtoList(skillDtos);

        assertEquals(2, result.get(0).getOffersAmount());
        assertEquals(3, result.size());
        assertEquals(THIRD_TITLE, result.get(2).getSkill().getTitle());
    }

    @Test
    @DisplayName("Testing empty mapping")
    void whenEmptySkillDtoListMappedToEmptySkillCandidateDtoListThenSuccess() {
        List<SkillDto> emptySkillsDto = new ArrayList<>();

        List<SkillCandidateDto> result = skillCandidateMapper.toSkillCandidateDtoList(emptySkillsDto);

        assertTrue(result.isEmpty());
    }

}