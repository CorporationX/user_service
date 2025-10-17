package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.repository.user.SkillRepository;

import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    @Value("${skill.offers.min.count}")
    private int minCountOffers;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillMapper skillMapper;

    @Override
    public SkillDto create(CreateSkillDto skillDto) {
        Skill skill = skillMapper.toSkill(skillDto);
        if (skillRepository.existsByTitle(skillDto.getTitle())) {
            throw new ForbiddenException("this skill already exists");
        }
        log.info("create skill {}", skillDto.getTitle());
        return skillMapper.toSkillDto(skillRepository.save(skill));
    }

    @Override
    public List<SkillDto> getByUserId(Long userId) {
        log.info("get list skills from user {}", userId);
        return skillMapper.toSkillsDto(skillRepository.findAllByUserId(userId));
    }

    @Override
    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        log.info("get offered skills from user {}", userId);
        return skillRepository.findSkillsOfferedToUser(userId).stream()
                .map(getSkillCandidateDtoFunction(userId)).toList();
    }

    private Function<Skill, SkillCandidateDto> getSkillCandidateDtoFunction(long userId) {
        return skill -> {
            SkillDto skillDto = skillMapper.toSkillDto(skill);
            int offersAmount = skillOfferRepository.countAllOffersOfSkill(skill.getId(), userId);
            return new SkillCandidateDto(skillDto, offersAmount);
        };
    }

    @Override
    public void acquireSkillFromOffers(long skillId, long userId) {
        if (skillOfferRepository.countAllOffersOfSkill(skillId, userId) < minCountOffers) {
            throw new ForbiddenException("Offers count should be more than " + minCountOffers);
        }
        log.info("user {} acquire skill {}", userId, skillId);
        skillRepository.assignSkillToUser(skillId, userId);
    }
}
