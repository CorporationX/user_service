package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private static final int MIN_SKILL_OFFERS = 3;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillMapper skillMapper, SkillOfferRepository skillOfferRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillOfferRepository = skillOfferRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.getTitle()))
            throw new RuntimeException("Skill with title '" + skillDto.getTitle() + "' already exists");

        Skill skill = skillMapper.toEntity(skillDto);
        Skill savedSkill = skillRepository.save(skill);
        return skillMapper.toDto(savedSkill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        // Получаем все скиллы, предложенные пользователю
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);
        return skills.stream()
                .collect(Collectors.groupingBy(Skill::getTitle))
                .entrySet().stream()
                .map(entry -> {
                    SkillDto skillDto = skillMapper.toDto(entry.getValue().get(0));
                    long offersAmount = entry.getValue().size();
                    return new SkillCandidateDto(skillDto, offersAmount);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> userSkill = skillRepository.findUserSkill(skillId, userId);
        if (userSkill.isPresent()) {
            return skillMapper.toDto(userSkill.get());
        }
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
        if (offers.size() <= MIN_SKILL_OFFERS) {
            return null;
        }
        skillRepository.assignSkillToUser(skillId, userId);
        Skill assignedSkill = skillRepository.findUserSkill(skillId, userId).orElseThrow();
        assignSkillGuarantees(skillId, userId, offers);
        return skillMapper.toDto(assignedSkill);
    }
    private void assignSkillGuarantees(long skillId, long userId, List<SkillOffer> offers) {
        for (SkillOffer offer : offers) {
            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .skill(Skill.builder().id(skillId).build())
                    .user(User.builder().id(userId).build())
                    .guarantor(offer.getRecommendation().getAuthor())
                    .build();
            userSkillGuaranteeRepository.save(guarantee);
        }
    }
}

