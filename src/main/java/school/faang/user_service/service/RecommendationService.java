package school.faang.user_service.service;

import jakarta.persistence.ManyToOne;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.utils.validator.ValidatorService;

import java.util.List;
import java.util.StringJoiner;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final ValidatorService validatorService;
    private final RecommendationMapper recommendationMapper;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillOfferMapper skillOfferMapper;

    public RecommendationDto create(RecommendationDto recommendationDto) {
        Recommendation recommendation = recommendationRepository
                .findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc
                        (recommendationDto.getAuthorId(), recommendationDto.getReceiverId())
                .orElseThrow(() -> new DataValidationException("Recommendation is not found"));

        validatorService.validateTime(recommendation);

        List<SkillOffer> listSkillOffer = recommendationDto.getSkillOffers() //все рекомендации скиллов от одного пользователя
                .stream()
                .map(skillOfferDto -> skillOfferMapper.toEntity(skillOfferDto, this))
                .toList();
        List<Skill> listSkill = listSkillOffer //все скиллы в рекомендации от пользователя
                .stream()
                .map(SkillOffer::getSkill)
                .toList();

        List<Skill> listSkillOfferFromDB = skillRepository.findAll(); //все навыки в системе которые существуют

        boolean doContainAll = listSkillOfferFromDB.containsAll(listSkill); //IDEA почему-то ругается и предлагает создать HashSet

        if (!doContainAll) {
            throw new DataValidationException("There is some skills missing");
        }

        listSkillOffer.forEach(
                skillOffer -> skillOfferRepository
                .create(skillOffer.getSkill().getId(), skillOffer.getRecommendation().getId())
        );
        return null;
    }

    public Skill getSkill(long skillId) {
        return skillRepository.findById(skillId)
                .orElseThrow(() -> new DataValidationException("Skill not exist"));
    }

    public User getUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

//    public void validateGuarantor(User user1, User user2){
//        if(user1.getRecommendationsGiven().containsAll(user2.getRecommendationsReceived())){
//        }
//    }
}
