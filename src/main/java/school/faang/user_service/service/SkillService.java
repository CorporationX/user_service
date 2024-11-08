package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SkillService {
    private static final long MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;
    private List<UserSkillGuarantee> list3;

    @Autowired
    public SkillService(SkillRepository skillRepository,
                        SkillMapper skillMapper,
                        SkillOfferRepository skillOfferRepository,
                        UserSkillGuaranteeRepository userSkillGuaranteeRepository,
                        UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillOfferRepository = skillOfferRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.userRepository = userRepository;
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle(skillDto.title())) {
            throw new DataValidationException();
        }
            Skill skillEntity = skillMapper.toEntity(skillDto);
            skillEntity = skillRepository.save(skillEntity);
            return skillMapper.toDto(skillEntity);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkills = skillRepository.findAllByUserId(userId);
        return allSkills.stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> allOfferedSkills = skillRepository.findSkillsOfferedToUser(userId);
        Map<Skill, Long> map2 = allOfferedSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()));
        List<SkillCandidateDto> listFinal = skillMapper.toCandidateDto(map2);
        return listFinal;
    }

   public SkillDto acquireSkillFromOffers(long skillId, Long userId) {
        Optional<Skill> requiredSkill = skillRepository.findUserSkill(skillId, userId);
        if (requiredSkill.isEmpty()) {
            List<SkillOffer> allOffers =
                    skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            long count = allOffers.size();
            if (count >= MIN_SKILL_OFFERS) {
                skillRepository.assignSkillToUser(skillId, userId);
                Skill skill1 = skillRepository.findById(skillId).orElseThrow(() ->
                        new RuntimeException("Скилл не найден"));
                User user4 = userRepository.findById(userId).orElseThrow(() ->
                        new RuntimeException("Пользователь не найден"));
                List<UserSkillGuarantee> list3 = skill1.getGuarantees();
                for (SkillOffer skillOffer : allOffers) {
                    UserSkillGuarantee obj = UserSkillGuarantee.builder()
                            .user(user4)
                            .skill(skill1)
                            .guarantor(skillOffer.getRecommendation().getAuthor())
                            .build();
                    userSkillGuaranteeRepository.save(obj);
                    list3.add(obj);
                }
                skill1.setGuarantees(list3);
                skillRepository.save(skill1);
                return skillMapper.toDto(skill1);
            }
        }
   }
}

