package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.service.skill.SkillService;
import school.faang.user_service.validation.SkillValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {
    @InjectMocks
    private SkillService skillService;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillValidator skillValidator;

    @Captor
    ArgumentCaptor<Skill> skillCaptor;

    Skill firstSkill;
    Skill secondSkill;
    List<Skill> skills;
    SkillDto firstSkillDto;
    SkillDto secondSkillDto;
    List<SkillDto> skillDtos;
    User user;
    User firstUser;
    User secondUser;

    @BeforeEach
    public void setup () {
        user = User.builder().id(1L).username("David").build();
        firstUser = User.builder().id(2L).username("Nikita").build();
        secondUser = User.builder().id(3L).username("Igor").build();

        firstSkill = Skill.builder().id(1L).title("java").users(List.of(user)).build();
        secondSkill = Skill.builder().id(2L).title("spring").users(List.of(user)).build();
        skills = List.of(firstSkill, secondSkill);

        firstSkillDto = SkillDto.builder().id(1L).title("java").userIds(List.of(1L)).build();
        secondSkillDto = SkillDto.builder().id(2L).title("spring").userIds(List.of(1L)).build();
        skillDtos = List.of(firstSkillDto, secondSkillDto);
    }

    @Test
    public void shouldThrowExceptionForExistingSkill () {
        SkillDto dto = setSkillDto(true);

        assertThrows(
                DataValidationException.class,
                () -> skillService.create(dto)
        );
    }

    @Test
    public void shouldCreateSkill () {
        SkillDto dto = setSkillDto(false);
        Skill skill = skillMapper.toEntity(dto);
        skill.setUsers(List.of());

        when(skillRepository.save(skillMapper.toEntity(dto))).thenReturn(skill);

        SkillDto result = skillService.create(dto);

        verify(skillRepository).save(skillCaptor.capture());
        Skill skillCaptured = skillCaptor.getValue();

        assertNotNull(result);
        assertEquals(dto.getTitle(), skillCaptured.getTitle());
    }

    @Test
    public void shouldReturnUserSkills () {
        user.setSkills(skills);

        when(skillRepository.findAllByUserId(user.getId())).thenReturn(skills);

        List<SkillDto> result = skillService.getUserSkills(user.getId());

        assertEquals(skillDtos, result);
    }

    @Test
    public void shouldReturnEmptyListOfUserSkills () {
        List<SkillDto> dtos = skillService.getUserSkills(user.getId());

        assertNotNull(dtos);
    }

    @Test
    public void shouldGetOfferedSkills () {
        SkillCandidateDto firstCandidate = SkillCandidateDto
                .builder().skill(skillMapper.toDto(firstSkill)).offersAmount(1L).build();
        SkillCandidateDto secondCandidate = SkillCandidateDto
                .builder().skill(skillMapper.toDto(secondSkill)).offersAmount(1L).build();
        List<SkillCandidateDto> candidates = List.of(firstCandidate, secondCandidate);

        when(skillRepository.findSkillsOfferedToUser(user.getId()))
                .thenReturn(skills);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(user.getId());
        assertEquals(candidates, result);
    }

    @Test
    public void shouldReturnEmptyListIfOfferedSkillsNotFound () {
        when(skillRepository.findSkillsOfferedToUser(1L)).thenReturn(List.of());
        List<SkillCandidateDto> result = skillService.getOfferedSkills(1L);

        assertEquals(List.of(), result);
    }

    @Test
    public void shouldGetSkillFromOffers () {
        Recommendation firstRec = Recommendation.builder()
                .id(1L)
                .author(firstUser)
                .receiver(secondUser)
                .build();
        SkillOffer firstOffer = SkillOffer.builder()
                .id(1L)
                .skill(firstSkill)
                .recommendation(firstRec)
                .build();
        Recommendation secondRec = Recommendation.builder()
                .id(2L)
                .author(secondUser)
                .receiver(user)
                .build();
        SkillOffer secondOffer = SkillOffer.builder()
                .id(2L)
                .skill(secondSkill)
                .recommendation(secondRec)
                .build();
        List<SkillOffer> offers = List.of(firstOffer, secondOffer, firstOffer);

        when(skillRepository.findById(1L)).thenReturn(Optional.of(firstSkill));
        when(skillOfferRepository.findAllOffersOfSkill(1L, 1L))
                .thenReturn(offers);

        SkillDto result = skillService.acquireSkillFromOffers(1L, 1L);
        assertEquals(skillMapper.toDto(firstSkill), result);
    }

    @Test
    public void shouldReturnSkillDtoIfUserHasSkill () {
        firstUser.setSkills(skills);

        when(skillRepository.findUserSkill(firstSkill.getId(), firstUser.getId()))
                .thenReturn(Optional.of(firstSkill));

        SkillDto result = skillService.acquireSkillFromOffers(firstSkill.getId(), firstUser.getId());

        assertEquals(result.getTitle(), firstSkillDto.getTitle());
        assertEquals(result.getId(), firstSkillDto.getId());
    }

    @Test
    public void shouldThrowExceptionOnInvalidSkillOffersSize () {
        assertThrows(
                DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1L, 1L)
        );
    }

    @Test
    public void shouldThrowExceptionIfSkillNotExist () {
        doThrow(DataValidationException.class)
                .when(skillRepository)
                .findById(1L);

        assertThrows(
                DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(1L, 1L)
        );
    }

    private SkillDto setSkillDto (boolean existsByTitle) {
        SkillDto dto = SkillDto.builder().id(1L).title("Title").build();

        if (!existsByTitle) {
            doNothing()
                    .when(skillValidator)
                    .checkIfSkillExists(dto.getTitle());
        } else {
            doThrow(DataValidationException.class)
                    .when(skillValidator)
                    .checkIfSkillExists(dto.getTitle());
        }
        return dto;
    }
}
