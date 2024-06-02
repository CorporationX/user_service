package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationMapper recommendationMapper;
    private final int FILTER_MONTH = 6;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        validationData(recommendationDto);
        Recommendation recommendation = fillEntityRecommendation(recommendationDto);

        recommendationRepository.save(recommendation);
        saveSkillOffers(recommendation);

        return recommendationMapper.toDto(recommendation);
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {
        validationData(recommendationDto);

        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        saveSkillOffers(recommendation);

        recommendationRepository.save(recommendation);
        return recommendationMapper.toDto(recommendation);
    }

    public void delete(long id) {
        validationBeforeDelete(id);
        recommendationRepository.deleteById(id);
    }

    public Page<RecommendationDto> getAllUserRecommendations(long receiverId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> receiverRecommendation = recommendationRepository.findAllByReceiverId(receiverId, pageable);
        return receiverRecommendation.map(recommendationMapper::toDto);
    }

    public Page<RecommendationDto> getAllGivenRecommendations(long authorId, int offset, int limit) {
        Pageable pageable = PageRequest.of(offset, limit);
        Page<Recommendation> authorRecommendation = recommendationRepository.findAllByAuthorId(authorId, pageable);
        return authorRecommendation.map(recommendationMapper::toDto);
    }

    private void saveSkillOffers(Recommendation recommendation) {
        long authorId = recommendation.getAuthor().getId();
        long receiverId = recommendation.getReceiver().getId();
        User user = userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException("User not found"));
        User guarantee = userRepository.findById(authorId)
                .orElseThrow(() -> new DataValidationException("Guarantee not found"));

        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        List<Skill> userSkills = userRepository.findById(receiverId)
                .orElseThrow(() -> new DataValidationException("Skills not found"))
                .getSkills();
        skillOffers.forEach(skillOffer -> {
            long skillId = skillOffer.getSkill().getId();
            if (userSkills.contains(skillId) && !userSkillGuaranteeRepository.existsById(authorId)) {
                Skill skill = skillRepository.findById(skillId).orElseThrow(() ->
                        new DataValidationException("Skill not found"));
                UserSkillGuarantee guaranteeSkill = new UserSkillGuarantee();

                guaranteeSkill.setSkill(skill);
                guaranteeSkill.setUser(user);
                guaranteeSkill.setGuarantor(guarantee);

                userSkillGuaranteeRepository.save(guaranteeSkill);
            } else {
                skillOfferRepository.save(skillOffer);
            }
        });
    }

    private void validationData(RecommendationDto recommendation) {
        LocalDate currentDate = LocalDate.now();
        boolean skillInSystem = recommendation.getSkillOffers().stream()
                .allMatch(skill -> skillOfferRepository.existsById(skill.getId()));
        if (!skillInSystem) {
            throw new DataValidationException("Skill was not found");
        }
        if (ChronoUnit.MONTHS.between(recommendation.getCreateAt().toLocalDate(), currentDate) <= FILTER_MONTH) {
            throw new DataValidationException("It has been less than 6 months since the last recommendation");
        }
    }

    private void validationBeforeDelete(long id) {
        if (recommendationRepository.findById(id).isEmpty()) {
            throw new DataValidationException("Recommendation not found");
        }
    }

    private Recommendation fillEntityRecommendation(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);

        recommendation.setReceiver(userRepository.findById(recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("User not found")));
        recommendation.setAuthor(userRepository.findById(recommendationDto.getAuthorId())
                .orElseThrow(() -> new DataValidationException(("Author not found"))));

        return recommendation;
    }
}
