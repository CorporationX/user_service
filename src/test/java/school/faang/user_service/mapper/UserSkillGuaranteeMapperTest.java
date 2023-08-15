package school.faang.user_service.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.UserSkillGuaranteeDto;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.mapper.skill.UserSkillGuaranteeMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserSkillGuaranteeMapperTest {
    @InjectMocks
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper = Mappers.getMapper(UserSkillGuaranteeMapper.class);

    @Test
    void testToEntity() {
        UserSkillGuaranteeDto userSkillGuaranteeDto = new UserSkillGuaranteeDto();
        userSkillGuaranteeDto.setId(1L);
        userSkillGuaranteeDto.setUserId(10L);
        userSkillGuaranteeDto.setSkillId(20L);
        userSkillGuaranteeDto.setGuarantorId(30L);

        UserSkillGuarantee userSkillGuarantee = userSkillGuaranteeMapper.toEntity(userSkillGuaranteeDto);

        assertEquals(1L, userSkillGuarantee.getId());
        assertEquals(10L, userSkillGuarantee.getUser().getId());
        assertEquals(20L, userSkillGuarantee.getSkill().getId());
        assertEquals(30L, userSkillGuarantee.getGuarantor().getId());
    }
}