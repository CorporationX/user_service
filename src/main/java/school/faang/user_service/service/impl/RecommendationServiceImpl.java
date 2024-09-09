package school.faang.user_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;

    @Override
    @Transactional
    public RecommendationDto createOrUpdate(RecommendationDto recommendationDto) {
        Recommendation recommendation;

        if (recommendationDto.getId() != null) {
            // Если ID рекомендации существует, значит нужно её обновить
            recommendation = recommendationRepository.findById(recommendationDto.getId())
                    .orElseThrow(() -> new DataValidationException("Recommendation not found"));
        } else {
            // Иначе создаём новую рекомендацию
            recommendation = new Recommendation();
            recommendation.setAuthor(userRepository.findById(recommendationDto.getAuthorId())
                    .orElseThrow(() -> new DataValidationException("Author not found")));
            recommendation.setReceiver(userRepository.findById(recommendationDto.getReceiverId())
                    .orElseThrow(() -> new DataValidationException("Receiver not found")));
            recommendation.setCreatedAt(LocalDateTime.now());
        }

        // Валидация содержания рекомендации
        if (recommendationDto.getContent() == null || recommendationDto.getContent().isEmpty()) {
            throw new DataValidationException("Recommendation content cannot be empty.");
        }

        // Обновляем контент рекомендации
        recommendation.setContent(recommendationDto.getContent());
        recommendation.setUpdatedAt(LocalDateTime.now());

        // Удаляем старые предложения скиллов (при обновлении)
        if (recommendation.getId() != null) {
            skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        }

        // Добавляем новые предложения скиллов
        for (SkillOfferDto skillOfferDto : recommendationDto.getSkillOffers()) {
            Skill skill = skillRepository.findById(skillOfferDto.getSkillId())
                    .orElseThrow(() -> new DataValidationException("Skill not found"));

            // Проверяем, есть ли уже скилл у пользователя
            if (skillRepository.findUserSkill(skill.getId(), recommendation.getReceiver().getId()).isPresent()) {
                // Добавляем автора как гаранта, если он еще не гарантировал этот скилл
                SkillOffer skillOffer = SkillOffer.builder()
                        .skill(skill)
                        .recommendation(recommendation)
                        .build();
                skillOfferRepository.save(skillOffer);
            } else {
                // Если у пользователя еще нет скилла, добавляем его
                skillRepository.assignSkillToUser(skill.getId(), recommendation.getReceiver().getId());

                SkillOffer skillOffer = SkillOffer.builder()
                        .skill(skill)
                        .recommendation(recommendation)
                        .build();
                skillOfferRepository.save(skillOffer);
            }
        }

        // Сохраняем рекомендацию
        recommendationRepository.save(recommendation);

        // Преобразуем в DTO и возвращаем
        return mapToDto(recommendation);
    }

    private RecommendationDto mapToDto(Recommendation recommendation) {
        return RecommendationDto.builder()
                .id(recommendation.getId())
                .authorId(recommendation.getAuthor().getId())
                .receiverId(recommendation.getReceiver().getId())
                .content(recommendation.getContent())
                .skillOffers(recommendation.getSkillOffers().stream()
                        .map(skillOffer -> SkillOfferDto.builder()
                                .skillId(skillOffer.getSkill().getId())
                                .build())
                        .toList())
                .createdAt(recommendation.getCreatedAt())
                .build();
    }

    @Override
    @Transactional
    public void delete(long id) {
        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Recommendation not found"));
        recommendationRepository.delete(recommendation);
    }

}
