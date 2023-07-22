package school.faang.user_service.utils.validator;

import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillOfferMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.List;

public class ValidatorService {
    public void validateTime(Recommendation recommendation){
        LocalDateTime recommendationCreate = recommendation.getCreatedAt();
        if(!recommendationCreate.isAfter(LocalDateTime.now().minusMonths(6))){
            throw new DataValidationException("Time has not expired");
        }
    }
    public void validateHaveSkillInSystem(RecommendationDto recommendationDto,
                                          SkillOfferMapper skillOfferMapper,
                                          SkillRepository skillRepository,
                                          RecommendationService recommendationService){

        List<SkillOffer> listSkillOffer = recommendationDto.getSkillOffers() //все рекомендации скиллов от одного пользователя
                .stream()
                .map(skillOfferDto -> skillOfferMapper.toEntity(skillOfferDto, recommendationService))
                .toList();
        List<Skill> listSkill = listSkillOffer //все скиллы в рекомендации от пользователя
                .stream()
                .map(SkillOffer::getSkill)
                .toList();

        List<Skill> listSkillOfferFromDB = skillRepository.findAll(); //все навыки в системе которые существуют

        boolean doContainAll = listSkillOfferFromDB.containsAll(listSkill); //IDEA почему-то ругается и предлагает создать HashSet

        if(!doContainAll){
            throw new DataValidationException("There is some skills missing");
        }
    }
}
