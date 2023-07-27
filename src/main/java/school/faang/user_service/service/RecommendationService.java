package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final SkillRepository skillRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final UserRepository userRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        Long id = recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
        Recommendation recommendation  = recommendationRepository.findById(id).orElseThrow();
        recommendation.setCreatedAt(LocalDateTime.now());
        recommendation.setSkillOffers(initNewSkillOffers(recommendation, recommendationDto.getSkillOffers()));
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    public RecommendationDto update(RecommendationDto updated) {
        if(updated.getId() == null){
            throw new DataValidationException("entity not found exception");
        }
        Recommendation recommendation = recommendationMapper.toEntity(updated);
        skillOfferRepository.deleteAllByRecommendationId(recommendation.getId());
        recommendation.setSkillOffers(initNewSkillOffers(recommendation, updated.getSkillOffers()));
        recommendation.setUpdatedAt(updated.getCreatedAt() !=null ? updated.getCreatedAt() : LocalDateTime.now());
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    private List<SkillOffer> initNewSkillOffers(Recommendation recommendation, List<SkillOfferDto> skillOffers) {
        validate(skillOffers);
        List<Skill> offeredSkills = skillRepository.findAllById(
                skillOffers.stream()
                .map(SkillOfferDto::getSkillId)
                .collect(Collectors.toList()));
        addGuarantee(recommendation, offeredSkills);
        return  skillOfferRepository.findAllById(
                offeredSkills.stream()
                        .map(s -> skillOfferRepository.create(s.getId(), recommendation.getId()))
                        .collect(Collectors.toList()));
    }

    private void addGuarantee(Recommendation recommendation, List<Skill> offeredSkills) {
        List<Skill> receiverSkills = recommendation.getReceiver().getSkills();
        List<UserSkillGuarantee> userSkillGuarantees = new ArrayList<>();
        offeredSkills.forEach(
                s -> {
                    if (receiverSkills.contains(s)) {
                        if (s.getGuarantees()
                                .stream()
                                .map(UserSkillGuarantee::getGuarantor)
                                .noneMatch(g -> g.equals(recommendation.getAuthor()))) {
                            UserSkillGuarantee userSkillGuarantee = UserSkillGuarantee.builder()
                                    .skill(s)
                                    .guarantor(recommendation.getAuthor())
                                    .user(recommendation.getReceiver())
                                    .build();
                            userSkillGuarantees.add(userSkillGuarantee);
                        }
                    }
                }
        );
        userSkillGuaranteeRepository.saveAll(userSkillGuarantees);
    }

    private void validate(List<SkillOfferDto> skills) {
        if (skills != null && !skills.isEmpty()) {
            List<Long> skillIds = skills.stream()
                    .map(SkillOfferDto::getSkillId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (skillRepository.countExisting(skillIds) != skills.size()) {
                throw new DataValidationException("list of skills contains not valid skills, please, check this");
            }
        }
    }
}
