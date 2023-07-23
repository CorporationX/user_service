package school.faang.user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.SkillValidator;

import java.util.List;

@Service

public class RecommendationService {
    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillValidator skillValidator;


    private final RecommendationMapper recommendationMapper = RecommendationMapper.INSTANCE;


    public RecommendationDto create(RecommendationDto recommendationDto) {
        skillValidator.validate(recommendationDto.getSkillOffers());
        Recommendation recommendation = recommendationMapper.toEntity(recommendationDto);
        User author = userRepository.findById(recommendationDto.getAuthorId()).orElseThrow();
        User receiver = userRepository.findById(recommendationDto.getReceiverId()).orElseThrow();
        recommendation.setAuthor(author);
        recommendation.setReceiver(receiver);
        List<SkillOffer> skillOffers = addGuarantee(recommendation);
        recommendation.setSkillOffers(skillOffers);
        return recommendationMapper.toDto(recommendationRepository.save(recommendation));
    }

    private List<SkillOffer> addGuarantee(Recommendation recommendation){
        List<Skill> userSkills = skillRepository.findAllByUserId(recommendation.getReceiver().getId());
        List<SkillOffer> skillOffers = recommendation.getSkillOffers();
        skillOffers.forEach(o -> {
            Skill skill = skillRepository.findById(o.getId()).orElseThrow();
            UserSkillGuarantee guarantee = new UserSkillGuarantee();
            guarantee.setSkill(skill);
            guarantee.setGuarantor(recommendation.getAuthor());
            guarantee.setUser(recommendation.getReceiver());
            if(userSkills.contains(skill)){
                if(!skill.getGuarantees().contains(guarantee)){
                    skill.getGuarantees().add(guarantee);
                }
            } else {
              skill.getGuarantees().add(guarantee);
            }
            o.setSkill(skill);
        });
        return skillOffers;
    }
}
