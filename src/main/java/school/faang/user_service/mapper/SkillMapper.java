package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.repository.UserRepository;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skill);

    default SkillCandidateDto toSkillCandidateDto(SkillDto skillDto, long count) {
        return new SkillCandidateDto(skillDto, count);
    }

    default UserSkillGuarantee toUserSkillGuarantee(UserRepository userRepository,
                                                    SkillOffer offer,
                                                    long userId) {
        return UserSkillGuarantee.builder()
                .user(userRepository.getReferenceById(userId))
                .skill(offer.getSkill())
                .guarantor(offer.getRecommendation().getAuthor())
                .build();
    }
}
