package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final RecommendationValidator recommendationValidator;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final SkillOfferRepository skillOfferRepository;

    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        try {
            validation(recommendationDto);
            Long recommendationId = recommendationRepository.create(recommendationDto.getAuthorId(),
                    recommendationDto.getReceiverId(),
                    recommendationDto.getContent());
            recommendationDto.setId(recommendationId);
            addSkillOffers(recommendationDto);
            List<Long> listGuaranteedSkills = getGuaranteedSkillIds(recommendationDto);
            saveMismatchedSkill(listGuaranteedSkills, recommendationDto);
            return recommendationDto;
        } catch (Exception e) {
            throw new DataValidationException("Ошибка при создании рекомендации: "+ e);
        }
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validation(recommendationDto);
        recommendationRepository.update(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(), recommendationDto.getContent());
        clearingSkills(recommendationDto);
        Recommendation updatedRecommendation = recommendationRepository
                .findById(recommendationDto.getId()).orElseThrow(() ->
                        new DataValidationException("Получатель отсутствует"));
        return recommendationMapper.toDto(updatedRecommendation);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        Page<Recommendation> entityRecommendation = recommendationRepository
                .findAllByReceiverId(recieverId, PageRequest.of(0, 1));
        return entityRecommendation.stream().map(page -> recommendationMapper.toDto(page)).toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> entityRecommendation = recommendationRepository.findAllByAuthorId(authorId,
                PageRequest.of(0, 1));
        return entityRecommendation.stream().map(page -> recommendationMapper.toDto(page)).toList();
    }

    public void addSkillOffers(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers().isEmpty()) {
            log.info("Список SkillOffersDto в dto пуст");
        }
        List<Long> skillsId = recommendationDto.getSkillOffers().stream().map(skillOfferDto ->
                skillOfferDto.getSkillId()).toList();
        skillsId.forEach(id -> skillOfferRepository.create(id, recommendationDto.getId()));
    }

    public void saveMismatchedSkill(List<Long> skillForSave, RecommendationDto recommendationDto) {
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Получатель отсутствует"));
        User guarantor = userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException("Гарантер отсутствует"));

        List<Skill> newSkills = new ArrayList<>();
        Set<Long> existingSkillIds = receiver.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());

        for (Long id : skillForSave) {
            if (existingSkillIds.contains(id)) continue;

            Skill skill = skillRepository.findById(id)
                    .orElseThrow(() -> new DataValidationException("Скилл не найден"));

            UserSkillGuarantee guarantee = UserSkillGuarantee.builder()
                    .user(receiver)
                    .skill(skill)
                    .guarantor(guarantor)
                    .build();
            userSkillGuaranteeRepository.save(guarantee);

            skill.getGuarantees().add(guarantee);
            skillRepository.save(skill);
            newSkills.add(skill);
            existingSkillIds.add(id);
        }

        if (!newSkills.isEmpty()) {
            List<Skill> updatedSkills = new ArrayList<>(receiver.getSkills());
            updatedSkills.addAll(newSkills);
            receiver.setSkills(updatedSkills);
            userRepository.save(receiver);
        }
    }

    public List<Long> getGuaranteedSkillIds(RecommendationDto recommendationDto) {
        Long guarantorId = recommendationDto.getAuthorId();
        User receiver = userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException("Получатель рекомендации отсутствует"));
        User guarantor = userRepository.findById(guarantorId)
                .orElseThrow(() -> new EntityNotFoundException("Гарантер рекомендации отсутствует"));

        List<Skill> allSkill = skillRepository.findAllByUserId(receiver.getId());
        List<Long> existingSkill = recommendationDto.getSkillOffers().stream().map(SkillOfferDto::getSkillId).toList();
        List<Skill> matchingSkills = allSkill.stream()
                .filter(skill -> existingSkill.stream().anyMatch(existing -> existing == skill.getId()))
                .toList();
        for (Skill skill : matchingSkills) {
            List<Long> skillGuarantorId = skill.getGuarantees().stream()
                    .map(UserSkillGuarantee -> UserSkillGuarantee.getGuarantor().getId()).toList();
            boolean isNotMatchGuarantor = skillGuarantorId.stream().noneMatch(id -> id.equals(guarantorId));
            if (isNotMatchGuarantor) {
                UserSkillGuarantee newUserSkillGuarantee = UserSkillGuarantee.builder().user(receiver)
                        .skill(skill)
                        .guarantor(guarantor)
                        .build();
                List<UserSkillGuarantee> newGuarantees = new ArrayList<>(skill.getGuarantees());
                newGuarantees.add(newUserSkillGuarantee);
                skill.setGuarantees(newGuarantees);
                userSkillGuaranteeRepository.save(newUserSkillGuarantee);
                skillRepository.save(skill);
            }
        }
        return existingSkill.stream()
                .filter(id -> matchingSkills.stream().noneMatch(existing -> existing.getId() == id))
                .toList();
    }

    public void clearingSkills(RecommendationDto recommendation) {
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        for (SkillOfferDto skillOffers : recommendation.getSkillOffers()) {
            skillOfferRepository.create(skillOffers.getSkillId(), recommendation.getId());
        }
        getGuaranteedSkillIds(recommendation);
    }

    private void validation(RecommendationDto recommendationDto){
        recommendationValidator.textAvailability(recommendationDto);
        recommendationValidator.checkRecommendationInterval(recommendationDto);
        recommendationValidator.checkingSkills(recommendationDto);
    }
}