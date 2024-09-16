package school.faang.user_service.service.recommendation.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.recommendation.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final RecommendationMapper recommendationMapper;

    @Override
    @Transactional
    public RecommendationDto createRecommendation(RecommendationDto recommendationDto) {
        log.info("Начало создания рекомендации от пользователя с id {} для пользователя с id {}",
                recommendationDto.authorId(), recommendationDto.receiverId());

        User author = userRepository.findById(recommendationDto.authorId())
                .orElseThrow(() -> {
                    log.error("Автор с id {} не найден", recommendationDto.authorId());
                    return new DataValidationException(
                            "Author with id " + recommendationDto.authorId() + " not found");
                });

        User receiver = userRepository.findById(recommendationDto.receiverId())
                .orElseThrow(() -> {
                    log.error("Получатель с id {} не найден", recommendationDto.receiverId());
                    return new DataValidationException(
                            "Receiver with id " + recommendationDto.receiverId() + " not found");
                });

        Recommendation recommendation = recommendationMapper.toRecommendation(recommendationDto);
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        recommendation.setCreatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        log.debug("Рекомендация с id {} успешно сохранена", recommendation.getId());

        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    public List<RecommendationDto> getAllUserRecommendations(long receiverId, Pageable pageable) {
        log.info("Получение всех рекомендаций для пользователя с id {}", receiverId);

        List<RecommendationDto> recommendations = recommendationRepository.findAllByReceiverId(receiverId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .toList();

        log.debug("Найдено {} рекомендаций для пользователя с id {}", recommendations.size(), receiverId);
        return recommendations;
    }

    @Override
    public List<RecommendationDto> getAllGivenRecommendations(long authorId, Pageable pageable) {
        log.info("Получение всех рекомендаций, созданных пользователем с id {}", authorId);

        List<RecommendationDto> recommendations = recommendationRepository.findAllByAuthorId(authorId, pageable)
                .stream()
                .map(recommendationMapper::toRecommendationDto)
                .toList();

        log.debug("Пользователь с id {} создал {} рекомендаций", authorId, recommendations.size());
        return recommendations;
    }

    @Override
    @Transactional
    public RecommendationDto updateRecommendation(long id, RecommendationDto recommendationDto) {
        log.info("Обновление рекомендации с id {}", id);

        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Рекомендация с id {} не найдена", id);
                    return new DataValidationException("Recommendation with id " + id + " not found");
                });

        recommendationMapper.updateFromDto(recommendationDto, recommendation);
        recommendation.setUpdatedAt(LocalDateTime.now());

        recommendationRepository.save(recommendation);
        log.debug("Рекомендация с id {} успешно обновлена", id);

        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        log.debug("Предложения по навыкам для рекомендации с id {} удалены", id);

        handleSkillOffers(recommendation, recommendationDto.skillOffers());

        return recommendationMapper.toRecommendationDto(recommendation);
    }

    @Override
    @Transactional
    public void deleteRecommendation(long id) {
        log.info("Удаление рекомендации с id {}", id);

        Recommendation recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Рекомендация с id {} не найдена", id);
                    return new DataValidationException("Recommendation with id " + id + " not found");
                });

        recommendationRepository.delete(recommendation);
        log.debug("Рекомендация с id {} успешно удалена", id);
    }

    private void handleSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        log.debug("Обработка предложений по навыкам для рекомендации с id {}", recommendation.getId());

        List<Long> skillIds = skillOffers.stream()
                .map(SkillOfferDto::skillId)
                .toList();

        List<Skill> skills = skillRepository.findAllById(skillIds);

        Map<Long, Skill> skillsMap = skills.stream()
                .collect(Collectors.toMap(Skill::getId, skill -> skill));

        List<SkillOffer> skillOffersToSave = skillOffers.stream().map(skillOfferDto -> {
            Skill skill = skillsMap.get(skillOfferDto.skillId());
            if (skill == null) {
                log.error("Навык с id {} не найден", skillOfferDto.skillId());
                throw new DataValidationException("Skill with id " + skillOfferDto.skillId() + " not found");
            }

            return SkillOffer.builder()
                    .skill(skill)
                    .recommendation(recommendation)
                    .build();
        }).toList();

        skillOfferRepository.saveAll(skillOffersToSave);
        log.debug("Предложения по навыкам для рекомендации с id {} успешно сохранены", recommendation.getId());
    }
}