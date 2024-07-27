package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillMapper skillMapper;
    @Captor
    private ArgumentCaptor<Skill> skillCaptor;
    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> guaranteesCaptor;
    @InjectMocks
    private SkillService skillService;

    private String title;
    private User firstUser, secondUser, thirdUser, fourthUser;
    private Skill firstSkill, secondSkill;
    private SkillDto firstSkillDto, secondSkillDto;
    private SkillOffer firstOffer, secondOffer, thirdOffer;

    @BeforeEach
    void setUp() {
        title = "title";

        firstUser = new User();
        firstUser.setId(1L);
        secondUser = new User();
        secondUser.setId(2L);
        thirdUser = new User();
        thirdUser.setId(3L);
        fourthUser = new User();
        fourthUser.setId(4L);

        firstSkill = new Skill();
        firstSkill.setId(1L);
        firstSkill.setTitle(title);
        firstSkill.setUsers(List.of(firstUser));
        secondSkill = new Skill();
        secondSkill.setId(2L);
        secondSkill.setTitle(title);
        secondSkill.setUsers(List.of(firstUser));

        firstSkillDto = new SkillDto();
        firstSkillDto.setId(1L);
        firstSkillDto.setTitle(title);
        firstSkillDto.setUserIds(List.of(firstUser.getId()));
        secondSkillDto = new SkillDto();
        secondSkillDto.setId(2L);
        secondSkillDto.setTitle(title);
        secondSkillDto.setUserIds(List.of(firstUser.getId()));

        Recommendation firstRecommendation = new Recommendation();
        firstRecommendation.setAuthor(secondUser);
        Recommendation secondRecommendation = new Recommendation();
        secondRecommendation.setAuthor(thirdUser);
        Recommendation thirdRecommendation = new Recommendation();
        thirdRecommendation.setAuthor(fourthUser);

        firstOffer = new SkillOffer(1L, firstSkill, firstRecommendation);
        secondOffer = new SkillOffer(2L, firstSkill, secondRecommendation);
        thirdOffer = new SkillOffer(3L, firstSkill, thirdRecommendation);
    }

    @Test
    void testCreateSkillWhenTitleIsNull() {
        firstSkillDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> skillService.createSkill(firstSkillDto));
        verify(userRepository, times(0)).findAllById(anyCollection());
        verify(skillRepository, times(0)).save(any());
    }

    @Test
    void testCreateSkillWhenTitleIsEmpty() {
        String emptyTitle = "";
        firstSkillDto.setTitle(emptyTitle);
        assertThrows(DataValidationException.class, () -> skillService.createSkill(firstSkillDto));
        verify(userRepository, times(0)).findAllById(anyCollection());
        verify(skillRepository, times(0)).save(any());
    }

    @Test
    void testCreateSkillWhenTitleIsBlank() {
        String blankTitle = "  ";
        firstSkillDto.setTitle(blankTitle);
        assertThrows(DataValidationException.class, () -> skillService.createSkill(firstSkillDto));
        verify(userRepository, times(0)).findAllById(anyCollection());
        verify(skillRepository, times(0)).save(any());
    }

    @Test
    void testCreateSkillWhenSkillAlreadyExists() {
        firstSkillDto.setTitle(title);
        when(skillRepository.existsByTitle(title)).thenReturn(true);

        assertThrows(DataValidationException.class, () -> skillService.createSkill(firstSkillDto));
        verify(userRepository, times(0)).findAllById(anyCollection());
        verify(skillRepository, times(0)).save(any());
    }

    @Test
    void testCreateSkill() {
        List<Long> userIds = List.of(firstUser.getId());
        List<User> users = List.of(firstUser);
        firstSkillDto.setUserIds(userIds);

        when(skillMapper.toEntity(firstSkillDto)).thenReturn(firstSkill);
        when(userRepository.findAllById(userIds)).thenReturn(users);
        when(skillRepository.save(firstSkill)).thenReturn(firstSkill);
        when(skillMapper.toDto(firstSkill)).thenReturn(firstSkillDto);

        SkillDto savedSkillDto = skillService.createSkill(firstSkillDto);

        assertEquals(firstSkillDto.getUserIds(), savedSkillDto.getUserIds());
        assertEquals(firstSkillDto.getTitle(), savedSkillDto.getTitle());
        verify(skillRepository).save(skillCaptor.capture());
        Skill capturedSkill = skillCaptor.getValue();
        assertEquals(firstSkill.getUsers(), capturedSkill.getUsers());
        verify(userRepository, times(1)).findAllById(firstSkillDto.getUserIds());
        verify(skillRepository, times(1)).save(firstSkill);
    }

    @Test
    void testGetUserSkills() {
        Long userId = firstUser.getId();
        List<SkillDto> skillDtos = List.of(firstSkillDto, secondSkillDto);
        List<Skill> skills = List.of(firstSkill, secondSkill);

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);
        when(skillMapper.toDto(skills)).thenReturn(skillDtos);
        List<SkillDto> actual = skillService.getUserSkills(userId);

        assertEquals(skillDtos, actual);
        assertEquals(userId, actual.get(0).getUserIds().get(0));
        verify(skillRepository).findAllByUserId(userId);
    }

    @Test
    void testGetOfferedSkills() {
        long userId = firstUser.getId();

        List<Skill> offeredSkills = List.of(firstSkill, secondSkill, secondSkill, firstSkill, firstSkill);
        List<SkillDto> offeredSkillDtos = List.of(
                firstSkillDto, secondSkillDto, secondSkillDto, firstSkillDto, firstSkillDto);
        SkillCandidateDto firstSkillCandidateDto = new SkillCandidateDto(firstSkillDto, 3L);
        SkillCandidateDto secondSkillCandidateDto = new SkillCandidateDto(secondSkillDto, 2L);
        List<SkillCandidateDto> candidateDtos = List.of(firstSkillCandidateDto, secondSkillCandidateDto);
        when(skillRepository.findSkillsOfferedToUser(userId)).thenReturn(offeredSkills);
        when(skillMapper.toDto(offeredSkills)).thenReturn(offeredSkillDtos);

        assertThat(candidateDtos).hasSameElementsAs(skillService.getOfferedSkills(userId));
        verify(skillRepository).findSkillsOfferedToUser(userId);
    }

    @Test
    void tesAcquireSkillFromOffersWhenSkillAlreadyAcquired() {
        long skillId = firstSkill.getId();
        long userId = firstUser.getId();
        firstSkill.setUsers(new ArrayList<>());
        firstSkillDto.setUserIds(new ArrayList<>());
        Optional<Skill> skillOptional = Optional.of(firstSkill);
        when(skillRepository.getById(skillId)).thenReturn(firstSkill);
        when(skillRepository.findUserSkill(skillId, userId)).thenReturn(skillOptional);
        when(skillMapper.toDto(firstSkill)).thenReturn(firstSkillDto);

        assertEquals(firstSkillDto, skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).getById(skillId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(userRepository, times(0)).getById(userId);
        verify(userSkillGuaranteeRepository, times(0)).saveAll(any());
        verify(skillRepository, times(0)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testAcquireSkillFromOffersWhenOffersLessThanNeeded() {
        long skillId = firstSkill.getId();
        long userId = firstUser.getId();
        firstSkill.setUsers(new ArrayList<>());
        firstSkillDto.setUserIds(new ArrayList<>());
        List<SkillOffer> offers = List.of(firstOffer, secondOffer);
        when(skillRepository.getById(skillId)).thenReturn(firstSkill);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        when(skillMapper.toDto(firstSkill)).thenReturn(firstSkillDto);

        assertEquals(firstSkillDto, skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(1)).getById(skillId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(userRepository, times(0)).getById(userId);
        verify(userSkillGuaranteeRepository, times(0)).saveAll(any());
        verify(skillRepository, times(0)).assignSkillToUser(skillId, userId);
    }

    @Test
    void testAcquireSkillFromOffers() {
        long skillId = firstSkill.getId();
        long userId = firstUser.getId();
        firstSkill.setUsers(List.of(firstUser));
        firstSkillDto.setUserIds(List.of(firstUser.getId()));
        List<SkillOffer> offers = List.of(firstOffer, secondOffer, thirdOffer);
        when(skillOfferRepository.findAllOffersOfSkill(skillId, userId)).thenReturn(offers);
        when(userRepository.getById(userId)).thenReturn(firstUser);
        when(skillRepository.getById(skillId)).thenReturn(firstSkill);
        when(skillMapper.toDto(firstSkill)).thenReturn(firstSkillDto);

        assertEquals(firstSkillDto, skillService.acquireSkillFromOffers(skillId, userId));
        verify(skillRepository, times(2)).getById(skillId);
        verify(skillRepository, times(1)).findUserSkill(skillId, userId);
        verify(skillOfferRepository, times(1)).findAllOffersOfSkill(skillId, userId);
        verify(userRepository, times(1)).getById(userId);
        verify(userSkillGuaranteeRepository, times(1)).saveAll(guaranteesCaptor.capture());
        List<UserSkillGuarantee> capturedGuarantees = guaranteesCaptor.getValue();
        assertEquals(offers.size(), capturedGuarantees.size());
        verify(skillRepository, times(1)).assignSkillToUser(skillId, userId);
    }
}