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
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;

@Component
public class SkillService {

    private SkillOfferRepository skillOfferRepository;
    private SkillRepository skillRepository;
    private SkillMapper mapper;

    @Autowired
    public SkillService(SkillOfferRepository skillOfferRepository, SkillMapper mapper, SkillRepository skillRepository) {
        this.skillOfferRepository = skillOfferRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper mapper) {
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
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isPresent()) {
            throw new DataValidationException("User skill already exist");
        }
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) >= MIN_SKILL_OFFERS) {
            skillRepository.assignSkillToUser(skillId, userId);
            List<SkillOffer> allOffersOfSkill = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            Skill skill = skillRepository.findById(skillId).orElseThrow();
            addGuarantees(skill, allOffersOfSkill, userId);
            return mapper.toDto(skill);
        }
        throw new DataValidationException("Not enough offers to add skill");
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
    public SkillDto create(SkillDto skill) {
        if (skillRepository.existsByTitle(skill.getTitle())) {
            throw new DataValidationException("This skill already exist");
        }
        Skill savedSkill = skillRepository.save(mapper.toEntity(skill));
        return mapper.toDto(savedSkill);

    private void addGuarantees(Skill skill, List<SkillOffer> offers, long userId) {
        User user = skill.getUsers()
                .stream()
                .filter(user1 -> user1.getId() == userId)
                .findFirst().orElseThrow(() -> new DataValidationException("User not found"));

        List<UserSkillGuarantee> listGuarantees = offers.stream().map(skillOffer -> UserSkillGuarantee.builder()
                        .user(user)
                        .skill(skill)
                        .guarantor(skillOffer.getRecommendation().getAuthor())
                        .build())
                .toList();

        skill.setGuarantees(listGuarantees);
    }
}
