package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Pageable.unpaged;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final static int COUNT_MONTHS = 6;

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validateRecommendation(recommendationDto);
        Recommendation recommendation = saveRecommendation(recommendationDto);

        List<Skill> skillOffers = recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .map(skillId -> skillRepository.findById(skillId).orElseThrow(() ->
                        new RuntimeException("Навык не найден"))).toList();

        List<Skill> userSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
        Map<Boolean, List<Skill>> partitionedSkills = skillOffers.stream()
                .collect(Collectors.partitioningBy(userSkills::contains));

        partitionedSkills.get(false)
                .stream().filter(Objects::nonNull)
                .forEach(skill -> skillOfferRepository.create(skill.getId(),
                        recommendation.getId()));

        partitionedSkills.get(true)
                .forEach(skill -> {
                    if (needsGuarantee(skill, recommendation.getAuthor(), recommendation.getReceiver())) {
                        addGuarantee(skill, recommendation.getAuthor(), recommendation.getReceiver());
                    }
                });

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendation) {
        if (!recommendationRepository.existsById(recommendation.getId())) {
            throw new DataValidationException("Рекомендации с ID " + recommendation.getId() + " не существует");
        }

        validateRecommendation(recommendation);

        recommendationRepository.update(
                recommendation.getAuthorId(),
                recommendation.getReceiverId(),
                recommendation.getContent()
        );

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        User author = userRepository.findById(recommendation.getAuthorId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Автор с ID " + recommendation.getAuthorId() + " не найден"));
        User receiver = userRepository.findById(recommendation.getReceiverId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Получатель с ID " + recommendation.getReceiverId() + " не найден"
                ));

        for (SkillOfferDto skillOfferDto : recommendation.getSkillOffers()) {
            Long skillId = skillOfferDto.getSkillId();
            skillOfferRepository.create(skillId, recommendation.getId());
            Skill skill = skillRepository.findById(skillId)
                    .orElseThrow(() -> new EntityNotFoundException("Навык с ID " + skillId + " не найден"));
            if (needsGuarantee(skill, author, receiver)) {
                addGuarantee(skill, author, receiver);
            }
        }
        Recommendation recommendationUpdate = recommendationRepository.findById(recommendation.getId())
                .orElseThrow(() -> new EntityNotFoundException("Рекомендации с ID "
                        + recommendation.getId() + " не существует"));
        return recommendationMapper.toDto(recommendationUpdate);
    }

    public void delete(long id) {
        recommendationRepository.deleteById(id);
    }

    public List<RecommendationDto> getAllUserRecommendations(long recieverId) {
        return getAllRecommendations(recommendationRepository::findAllByReceiverId, recieverId);
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return getAllRecommendations(recommendationRepository::findAllByAuthorId, authorId);
    }

    private List<RecommendationDto> getAllRecommendations(BiFunction<Long, Pageable, Page<Recommendation>> method, long id) {
        return method.apply(id, unpaged())
                .toList().stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private Recommendation saveRecommendation(RecommendationDto recommendationDto) {
        Long newIdRecommendation = recommendationRepository.create(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent()
        );
        return recommendationRepository.findById(newIdRecommendation)
                .orElseThrow(() -> new RuntimeException("Не удалось найти созданную рекомендацию"));
    }

    private boolean needsGuarantee(Skill skill, User guarantor, User receiver) {
        List<UserSkillGuarantee> guarantees = skill.getGuarantees();
        if (skill.getGuarantees() == null) {
            return false;
        }
        return guarantees.stream()
                .noneMatch(userSkillGuarantee ->
                        userSkillGuarantee.getUser().equals(receiver) &&
                                userSkillGuarantee.getGuarantor().equals(guarantor));
    }

    private void addGuarantee(Skill skill, User guarantor, User receiver) {
        userSkillGuaranteeRepository.save(UserSkillGuarantee.builder()
                .user(receiver)
                .skill(skill)
                .guarantor(guarantor)
                .build());
    }

    private boolean isRecentRecommendationExists(RecommendationDto recommendationDto) {
        LocalDate monthsAgo = LocalDate.now().minusMonths(COUNT_MONTHS);
        User receiver = userRepository.getReferenceById(recommendationDto.getReceiverId());

        return receiver.getRecommendationsReceived().stream()
                .filter(recommendationReceived ->
                        recommendationReceived.getAuthor().getId().equals(recommendationDto.getAuthorId()))
                .anyMatch(recommendationReceived ->
                        recommendationReceived.getCreatedAt().isAfter(monthsAgo.atStartOfDay()));
    }

    private boolean isAllSkillsExist(RecommendationDto recommendationDto) {
        if (recommendationDto.getSkillOffers() == null || recommendationDto.getSkillOffers().isEmpty()) {
            return true;
        }
        return recommendationDto.getSkillOffers().stream()
                .map(SkillOfferDto::getSkillId)
                .allMatch(skillRepository::existsById);
    }

    private void validateRecommendation(RecommendationDto recommendationDto) {
        if (!isAllSkillsExist(recommendationDto)) {
            throw new DataValidationException("Один или несколько навыков не существуют в системе");
        } else if (isRecentRecommendationExists(recommendationDto)) {
            throw new DataValidationException("Автор дает рекомендацию раньше," +
                    "чем через 6 месяцев после его последней рекомендации этому пользователю.");
        }
    }
}
