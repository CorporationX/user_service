package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository, SkillOfferRepository skillOfferRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillOfferRepository = skillOfferRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.userRepository = userRepository;
    }

    public SkillDto create(SkillDto skillDTO) {
        if (!skillRepository.existsByTitle(skillDTO.getTitle())) {
            Skill skill = skillRepository.save(SkillMapper.INSTANCE.toEntity(skillDTO));
            return SkillMapper.INSTANCE.toSkillDTO(skill);
        } else {
            throw new DataValidException("Skill already exists");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillRepository.findAllByUserId(userId)
                .stream()
                .map(SkillMapper.INSTANCE::toSkillDTO)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillOfferRepository.findAllOffersToUser(userId)
                .stream()
                .collect(Collectors.groupingBy(SkillOffer::getSkill, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> SkillCandidateDto.builder()
                        .skill(SkillMapper.INSTANCE.toSkillDTO(entry.getKey()))
                        .offersAmount(entry.getValue())
                        .build())
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
            List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
            User user = userRepository.findById(userId).orElseThrow();

            if (offers.size() >= 3) {
                skillRepository.assignSkillToUser(skillId, userId);

                offers.forEach(offer -> {
                    userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                            .user(user)
                            .skill(skillRepository.findById(skillId).orElseThrow())
                            .guarantor(offer.getRecommendation().getAuthor())
                            .build());

                    skillOfferRepository.deleteById(offer.getId());
                });
            }
        }

        return SkillMapper.INSTANCE.toSkillDTO(
                skillRepository.findUserSkill(skillId, userId).orElseThrow()
        );
    }
}
