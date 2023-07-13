package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class SkillService {
    private SkillRepository skillRepository;
    private SkillOfferRepository skillOfferRepository;
    private SkillMapper mapper;

    @Autowired
    public SkillService(SkillOfferRepository skillOfferRepository, SkillMapper mapper, SkillRepository skillRepository) {
        this.skillOfferRepository = skillOfferRepository;
        this.skillRepository = skillRepository;
        this.mapper = mapper;
    }


    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<SkillDto> userSkills = getUserSkills(userId);
        List<SkillCandidateDto> offeredSkills = new ArrayList<>();
        userSkills.stream()
                .map((skillDto)-> offeredSkills
                        .add(new SkillCandidateDto(skillDto,skillOfferRepository
                                .countAllOffersOfSkill(skillDto.getId(),userId))));
        return offeredSkills;
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> allByUserId = skillRepository.findAllByUserId(userId);
        verifyUserExist(allByUserId);
        return mapSkillToDto(allByUserId);
    }

    private void verifyUserExist(List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            throw new DataValidationException("User with that id doesn't exist");
        }
    }

    private List<SkillDto> mapSkillToDto(List<Skill> skills) {
        return skills.stream()
                .map((skill -> mapper.toDto(skill)))
                .toList();
    }
}