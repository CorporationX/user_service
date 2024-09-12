package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillDto;
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
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SkillService {
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;


    public SkillDto create(SkillDto skill) {
        validateSkill(skill);
        if (!skillRepository.existsByTitle(skill.getTitle())) {
            Skill skillToAdd = skillMapper.toEntity(skill);
            return skillMapper.toDto(skillRepository.save(skillToAdd));
        } else {
            throw new DataValidationException("Такой скилл уже существует");
        }

    }

    private void validateSkill(SkillDto skill) {
        if (skill.getTitle() == null || skill.getTitle().isBlank()) {
            throw new DataValidationException("Название скила не должно быть пустым");
        }
    }

    public List<SkillDto> getUserSkills(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataValidationException("Указан несуществующий пользователь");
        } else {
            List<SkillDto> skills = skillRepository.findAllByUserId(userId).stream().map(skill -> skillMapper.toDto(skill)).toList();
            if (!skills.isEmpty()) {
                return skills;
            } else {
                throw new DataValidationException("У пользователя нет умений");
            }
        }

    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Optional<Skill> skill = skillRepository.findById(skillId);
        Optional<User> user = userRepository.findById(userId);
        if (skill.isEmpty()) {
            throw new DataValidationException("Указанный скилл не существует");
        } else if (user.isEmpty()) {
            throw new DataValidationException("Указанный пользователь не существует");
        } else {
            if (skillRepository.findUserSkill(skillId, userId).isEmpty()) {
                List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill(skillId, userId);
                if (offers.size() >= 3) {
                    skillRepository.assignSkillToUser(skillId, userId);
                    for (SkillOffer offer : offers) {
                        UserSkillGuarantee guarantee = new UserSkillGuarantee();
                        guarantee.setSkill(skill.get());
                        guarantee.setUser(user.get());
                        guarantee.setGuarantor(offer.getRecommendation().getAuthor());
                        userSkillGuaranteeRepository.save(guarantee);

                    }
                    return skillMapper.toDto(skill.get());
                } else {
                    throw new DataValidationException("Скилл предложен менее 3 раз");
                }
            } else {
                throw new DataValidationException("У пользователя уже есть данный скилл");
            }
        }
    }


}
