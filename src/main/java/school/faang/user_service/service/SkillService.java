package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillDtoSkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkillService {
    private static final long MIN_SKILL_OFFERS = 3;

    private final SkillRepository skillRepository;
    private final SkillDtoSkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, SkillDtoSkillMapper skillMapper, SkillOfferRepository skillOfferRepository, UserSkillGuaranteeRepository userSkillGuaranteeRepository,
                        UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.skillMapper = skillMapper;
        this.skillOfferRepository = skillOfferRepository;
        this.userSkillGuaranteeRepository = userSkillGuaranteeRepository;
        this.userRepository = userRepository;
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepository.existsByTitle( skillDto.getTitle() )) {
            throw new DataValidationException( "Skill with such name already exist!" );
        }
        Skill newSkill = skillRepository.save( skillMapper.toSkill( skillDto ) );
        return skillMapper.toSkillDto( newSkill );
    }

    public List<SkillDto> getUserSkills(long userId) {
        List<Skill> skills = skillRepository.findAllByUserId( userId );
        if (skills == null) {
            throw new DataValidationException( "Skill list is null" );
        }
        return skills.stream()
                .map( skillMapper::toSkillDto )
                .toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        List<Skill> offeredSkillsToUser = skillRepository.findSkillsOfferedToUser( userId );

        if (offeredSkillsToUser == null) {
            throw new DataValidationException( "Offered skills list is null" );
        }
        Map<Long, Long> skillCountMap = offeredSkillsToUser.stream()
                .collect( Collectors.groupingBy( Skill::getId, Collectors.counting() ) );

        return offeredSkillsToUser.stream()
                .distinct()
                .map( skillMapper::toCandidateDto )
                .peek( skillCandidateDto -> {
                    long countOffers = skillCountMap.get( skillCandidateDto.getSkill().getId() );
                    skillCandidateDto.setOffersAmount( countOffers );
                } )
                .toList();
    }

    @Transactional
    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        User user = getUserById( userId );
        Skill skill = getSkillById( skillId );
        validateUserSkillNotExist( skillId, userId );
        validateSkillOffersCount( skillId, userId );
        assignSkillToUser( skillId, userId );
        saveUserSkillGuarantees( skillId, userId );
        return skillMapper.toSkillDto( skill );
    }

    private User getUserById(long userId) {
        return userRepository.findById( userId )
                .orElseThrow( () -> new EntityNotFoundException( "User with id " + userId + " does not exist" ) );
    }

    private Skill getSkillById(long skillId) {
        return skillRepository.findById( skillId )
                .orElseThrow( () -> new EntityNotFoundException( "Skill with id " + skillId + " does not exist" ) );
    }

    private void validateUserSkillNotExist(long skillId, long userId) {
        skillRepository.findUserSkill( skillId, userId )
                .ifPresent( skill -> {
                    throw new DataValidationException( "User already has that skill" );
                } );
    }

    private void validateSkillOffersCount(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill( skillId, userId );
        if (offers.size() < MIN_SKILL_OFFERS) {
            throw new DataValidationException( "The skill offered less than 3 times" );
        }
    }

    private void assignSkillToUser(long skillId, long userId) {
        skillRepository.assignSkillToUser( skillId, userId );
    }

    private void saveUserSkillGuarantees(long skillId, long userId) {
        List<SkillOffer> offers = skillOfferRepository.findAllOffersOfSkill( skillId, userId );
        User user = getUserById( userId );
        Skill skill = getSkillById( skillId );
        offers.forEach( offer -> {
            User guarantor = getUserById( offer.getRecommendation().getAuthor().getId() );
            userSkillGuaranteeRepository.save( new UserSkillGuarantee( null, user, skill, guarantor ) );
        } );
    }
}
