package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.skill.SkillRepository;
import school.faang.user_service.repository.skill.SkillRequestRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;
    private final SkillValidator skillValidator;

    private final SkillRequestRepository skillRequestRepository;

    public SkillDto create(SkillDto skillDto) {
        skillValidator.validateExistTitle(skillDto.getTitle());

        Skill skill = skillRepository.save(skillMapper.dtoToEntity(skillDto));
        return skillMapper.entityToDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId(userId);

        return skills.stream()
                .map(skillMapper::entityToDto)
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> skills = skillRepository.findSkillsOfferedToUser(userId);

        return skills.stream()
                .collect(Collectors.groupingBy(skillMapper::entityToDto, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> new SkillCandidateDto(entry.getKey(), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        skillValidator.validateUserSkillExist(skillId, userId);
        skillValidator.validateSkillOfferCount(skillId, userId);

        skillRepository.assignSkillToUser(skillId, userId);
        Optional<Skill> skill = skillRepository.findUserSkill(skillId, userId);
        return skill.map(skillMapper::entityToDto)
                .orElseThrow(() -> new DataValidationException("Скилл не найден"));
    }

    public List<SkillRequest> findByRequestId(long requestId) {
        return skillRequestRepository.findByRequestId(requestId);
    }

    public SkillRequest createRequest(long requestId, long skillId) {
        return skillRequestRepository.create(requestId, skillId);
    }

    public Optional<Skill> findUserSkill(long userId, long skillId) {
        return skillRepository.findUserSkill(userId, skillId);
    }

    public boolean existsById(long id) {
        return skillRepository.existsById(id);
    }

    public void assignSkillToUser(long skillId, long receiverId) {
        skillRepository.assignSkillToUser(skillId, receiverId);
    }

    public int countExisting(List<Long> ids){
        return skillRepository.countExisting(ids);
    }

    public List<Skill> getSkillsByIdIn(Collection<Long> ids) {
        List<Skill> skills = skillRepository.findByIdIn(ids);
        Set<Long> existingSkillIds = skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        String notValidSkillIds = ids.stream()
                .filter(id -> !existingSkillIds.contains(id))
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        if (!notValidSkillIds.isEmpty()) {
            throw new EntityNotFoundException("Skills with this ids don't exist: " + notValidSkillIds);
        }

        return skills;
    }
}
