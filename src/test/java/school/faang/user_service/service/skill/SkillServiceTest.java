package school.faang.user_service.service.skill;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.SkillService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);


    private Skill skill;
    private long userId = 1L;
    private long skillId = 1L;

    @BeforeEach
    public void setUp() {
        skill = new Skill();
        skill.setId(skillId);
    }

    @Test
    public void testCreateExistsByTitle() {
        skill.setTitle("Java");
        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.createSkill(skill));
    }

    @Test
    public void testCreateSkill() {
        skill.setTitle("Java");

        when(skillRepository.existsByTitle(skill.getTitle())).thenReturn(false);
        when(skillRepository.save(skill)).thenReturn(skill);

        Skill result = skillService.createSkill(skill);

        verify(skillRepository, times(1)).save(skill);
        assertEquals(result, skill);
    }

    @Test
    public void testGetUserSkills() {

        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findAllByUserId(userId)).thenReturn(List.of(skill));

        Skill returnedSkill = skillService.getUserSkills(userId).get(0);

        verify(skillRepository, times(1)).findAllByUserId(userId);
        assertEquals(skill, returnedSkill);
    }

    @Test
    public void testGetOfferedSkills() {
        List<SkillDto> skillDtos = LongStream.of(1L, 2L, 3L)
                .mapToObj(l -> {
                    Skill s = new Skill();
                    s.setId(l);
                    s.setTitle("Skill" + l);
                    return skillMapper.toDto(s);
                })
                .toList();

        List<SkillCandidateDto> expectedList = Arrays.asList(
                new SkillCandidateDto(skillDtos.get(0), 2L),
                new SkillCandidateDto(skillDtos.get(1), 1L),
                new SkillCandidateDto(skillDtos.get(2), 2L)
        );

        List<Skill> skills = LongStream.of(1L, 2L, 3L, 1L, 3L)
                .mapToObj(l -> {
                    Skill s = new Skill();
                    s.setId(l);
                    s.setTitle("Skill" + l);
                    return s;
                })
                .toList();

        when(userRepository.existsById(userId)).thenReturn(true);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(skills);

        List<Skill> offeredSkills = skillService.getOfferedSkills(userId);
        List<SkillCandidateDto> resultList = new ArrayList<>(skillMapper.toCandidateDtoList(offeredSkills));

        resultList.sort(Comparator.comparingLong(skill -> skill.getSkillDto().getId()));

        assertIterableEquals(expectedList, resultList);
    }

    @Test
    public void testCreateSkillFailingValidation() {
        User receiver = new User();
        receiver.setId(userId);

        List<User> users = LongStream.of(2L, 3L, 4L)
                .mapToObj(l -> {
                    User u = new User();
                    u.setId(l);
                    return u;
                }).toList();

        List<Recommendation> recommendations = users.stream()
                .map(u -> {
                    Recommendation r = new Recommendation();
                    r.setId(u.getId());
                    r.setAuthor(u);
                    r.setReceiver(receiver);
                    return r;
                }).toList();

        List<SkillOffer> skillOffers = recommendations.stream()
                .map(r -> new SkillOffer(r.getId(), skill, r)).toList();

        List<UserSkillGuarantee> guarantees = skillOffers.stream()
                .map(offer -> UserSkillGuarantee.builder()
                        .skill(skill)
                        .guarantor(offer.getRecommendation().getAuthor())
                        .user(offer.getRecommendation().getReceiver())
                        .build())
                .distinct()
                .toList();

        when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(Optional.empty());
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(skillOffers);
        when(userSkillGuaranteeRepository.saveAll(guarantees)).thenReturn(guarantees);

        Skill skill1 = skillService.acquireSkillFromOffers(skillId, userId);
        Assertions.assertThat(skill)
                .usingRecursiveComparison()
                .ignoringFields("createdAt", "updatedAt")
                .isEqualTo(skill1);
    }
}