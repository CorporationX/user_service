package school.faang.user_service.service.skill;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.skill.CreateSkillDto;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.entity.user.Skill;
import school.faang.user_service.entity.user.User;
import school.faang.user_service.entity.user.UserSkillGuarantee;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.ForbiddenException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.mapper.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.user.SkillRepository;
import school.faang.user_service.service.skill_offer.SkillOfferService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.service.user.skill_guarantee.UserSkillGuaranteeService;
import school.faang.user_service.validation.SkillValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SkillServiceTest {

    @InjectMocks
    private SkillServiceImpl skillService;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillOfferService skillOfferService;

    @Mock
    private UserSkillGuaranteeService userSkillGuaranteeService;

    @Spy
    private SkillMapper skillMapper = Mappers.getMapper(SkillMapper.class);

    @Spy
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper = Mappers.getMapper(UserSkillGuaranteeMapper.class);

    @Mock
    private UserService userService;

    @Mock
    private SkillValidator skillValidator;

    @Captor
    private ArgumentCaptor<List<UserSkillGuarantee>> userSkillGuaranteeCaptor;

    private static final long SKILL_ID = 10L;
    private static final String SKILL_TITLE = "Java";
    private static final long USER_ID = 5L;
    private static final long GUARANTOR_ID = 11L;
    private static final long AUTHOR_ID = 100L;
    private static final String USER_NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String ABOUT_ME = "aboutMe";
    private static final String SKILL_ALREADY_EXISTS_MESSAGE = "Skill with title: %s already exists.";
    private static final String SKILL_NOT_FOUND_MESSAGE = "Skill with id %d does not exist.";
    private static final String USER_ALREADY_HAS_SKILL_MESSAGE =
            "User already has this skill.";
    private static final String NOT_ENOUGH_OFFERS_MESSAGE =
            "Skill cannot be acquired. At least 3 unique users must offer this skill.";

    @Test
    @DisplayName("Should create a new skill when title does not exist")
    public void createSkillCreatesNewSkill() {
        CreateSkillDto createSkillDto = createSkillDto();
        Skill savedSkill = createSkill();

        when(skillRepository.existsByTitle(SKILL_TITLE)).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenReturn(savedSkill);

        SkillDto result = skillService.create(createSkillDto);

        assertNotNull(result);
        assertEquals(SKILL_ID, result.id());
        assertEquals(SKILL_TITLE, result.title());

        verify(skillRepository).existsByTitle(SKILL_TITLE);
        verify(skillMapper).toSkill(createSkillDto);
        verify(skillRepository).save(any(Skill.class));
        verify(skillMapper).toSkillDto(savedSkill);
    }

    @Test
    @DisplayName("Should throw exception when skill title already exists")
    public void createSkillThrowsExceptionWhenTitleAlreadyExists() {
        CreateSkillDto createSkillDto = createSkillDto();

        when(skillRepository.existsByTitle(SKILL_TITLE)).thenReturn(true);

        doThrow(new DataValidationException(String.format(SKILL_ALREADY_EXISTS_MESSAGE, SKILL_TITLE)))
                .when(skillValidator)
                .validateSkillTitleIsUnique(true, SKILL_TITLE);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> skillService.create(createSkillDto)
        );

        assertEquals(String.format(SKILL_ALREADY_EXISTS_MESSAGE, SKILL_TITLE), exception.getMessage());

        verify(skillMapper, never()).toSkill(any());
        verify(skillRepository, never()).save(any());
        verify(skillMapper, never()).toSkillDto(any());
    }

    @Test
    @DisplayName("Should return skills with guarantors for given user ID")
    public void getByUserIdReturnsSkillsWithGuarantors() {
        User user = createUser(USER_ID);
        User guarantor = createUser(GUARANTOR_ID);
        UserSkillGuarantee guarantee = createUserSkillGuarantee(user, guarantor);
        Skill skill = createSkillWithGuarantees(List.of(guarantee));

        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of(skill));

        UserDto guarantorDto = createUserDto();

        when(userService.getUser(GUARANTOR_ID)).thenReturn(guarantorDto);

        List<SkillDto> result = skillService.getByUserId(USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());

        SkillDto skillDto = result.get(0);
        assertEquals(SKILL_ID, skillDto.id());
        assertEquals(SKILL_TITLE, skillDto.title());
        assertEquals(List.of(guarantorDto), skillDto.guarantors());

        verify(skillRepository).findAllByUserId(USER_ID);

        verify(userService).getUser(GUARANTOR_ID);
    }

    @Test
    @DisplayName("Should return empty list when user has no skills")
    public void getByUserIdReturnsEmptyListWhenNoSkills() {
        when(skillRepository.findAllByUserId(USER_ID)).thenReturn(List.of());

        List<SkillDto> result = skillService.getByUserId(USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(skillRepository).findAllByUserId(USER_ID);
        verifyNoInteractions(userService);
        verify(skillMapper, never()).toSkillDtoWithGuarantors(any(), anyList());
    }

    @Test
    @DisplayName("Should return list of offered skills for user")
    void getOfferedSkillsReturnsListOfSkills() {
        Skill skill = createSkill();
        int offersAmount = 3;

        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of(skill));
        when(skillOfferService.countAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offersAmount);

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());

        SkillCandidateDto skillCandidateDto = result.get(0);
        assertNotNull(skillCandidateDto.skill());
        assertEquals(SKILL_ID, skillCandidateDto.skill().id());
        assertEquals(SKILL_TITLE, skillCandidateDto.skill().title());
        assertEquals(offersAmount, skillCandidateDto.offersAmount());

        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
        verify(skillOfferService).countAllOffersOfSkill(SKILL_ID, USER_ID);
    }

    @Test
    @DisplayName("Should return empty list when no offered skills found for user")
    void getOfferedSkillsReturnsEmptyListWhenNoSkills() {
        when(skillRepository.findSkillsOfferedToUser(USER_ID)).thenReturn(List.of());

        List<SkillCandidateDto> result = skillService.getOfferedSkills(USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(skillRepository).findSkillsOfferedToUser(USER_ID);
        verifyNoInteractions(skillOfferService);
    }

    @Test
    @DisplayName("Should throw exception when skill does not exist during acquisition")
    void acquireSkillFromOffersThrowsExceptionWhenSkillNotExists() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(false);

        doThrow(new DataValidationException(String.format(SKILL_NOT_FOUND_MESSAGE, SKILL_ID)))
                .when(skillValidator)
                .ensureSkillExists(false, SKILL_ID);

        DataValidationException exception = assertThrows(
                DataValidationException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertEquals(String.format(SKILL_NOT_FOUND_MESSAGE, SKILL_ID), exception.getMessage());

        verify(skillRepository).existsById(SKILL_ID);
        verifyNoInteractions(skillOfferService, userSkillGuaranteeMapper, userSkillGuaranteeService);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user already has the skill")
    void acquireSkillFromOffersThrowsExceptionWhenUserAlreadyHasSkill() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        when(skillRepository.existsUserSkill(SKILL_ID, USER_ID)).thenReturn(true);

        doNothing().when(skillValidator).ensureSkillExists(true, SKILL_ID);
        doThrow(new ForbiddenException(USER_ALREADY_HAS_SKILL_MESSAGE))
                .when(skillValidator).validateUserDoesNotHaveSkill(true, SKILL_ID, USER_ID);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertEquals(USER_ALREADY_HAS_SKILL_MESSAGE, exception.getMessage());

        verify(skillRepository).existsById(SKILL_ID);
        verify(skillRepository).existsUserSkill(SKILL_ID, USER_ID);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verifyNoInteractions(skillOfferService, userSkillGuaranteeMapper, userSkillGuaranteeService);
    }

    @Test
    @DisplayName("Should throw exception when not enough offers to acquire skill")
    void acquireSkillFromOffersThrowsExceptionWhenNotEnoughOffers() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        when(skillRepository.existsUserSkill(SKILL_ID, USER_ID)).thenReturn(false);

        User author = createUser(AUTHOR_ID);
        Recommendation recommendation = createRecommendation(author);
        SkillOffer offer = createSkillOffer(recommendation);

        List<SkillOffer> offers = List.of(offer);

        when(skillOfferService.getAllOffersOfSkill(SKILL_ID, USER_ID)).thenReturn(offers);

        doNothing().when(skillValidator).ensureSkillExists(true, SKILL_ID);
        doNothing().when(skillValidator).validateUserDoesNotHaveSkill(false, SKILL_ID, USER_ID);
        doThrow(new ForbiddenException(NOT_ENOUGH_OFFERS_MESSAGE))
                .when(skillValidator).validateEnoughSkillOffers(offers);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> skillService.acquireSkillFromOffers(SKILL_ID, USER_ID)
        );

        assertEquals(NOT_ENOUGH_OFFERS_MESSAGE, exception.getMessage());

        verify(skillRepository).existsById(SKILL_ID);
        verify(skillRepository).existsUserSkill(SKILL_ID, USER_ID);
        verify(skillOfferService).getAllOffersOfSkill(SKILL_ID, USER_ID);
        verify(skillRepository, never()).assignSkillToUser(anyLong(), anyLong());
        verifyNoInteractions(userSkillGuaranteeMapper, userSkillGuaranteeService);
    }

    @Test
    @DisplayName("Should successfully assign skill to user when all conditions are met")
    void acquireSkillFromOffersAssignsSkillWhenValid() {
        when(skillRepository.existsById(SKILL_ID)).thenReturn(true);
        when(skillRepository.existsUserSkill(SKILL_ID, USER_ID)).thenReturn(false);

        Skill skill = createSkill();
        User receiver = createUser(USER_ID);
        User author = createUser(AUTHOR_ID);

        Recommendation recommendation = createRecommendation(author);
        recommendation.setReceiver(receiver);

        SkillOffer offer = createSkillOffer(recommendation);
        offer.setSkill(skill);

        List<SkillOffer> offers = List.of(offer);

        when(skillOfferService.getAllOffersOfSkill(SKILL_ID, USER_ID))
                .thenReturn(offers);

        doNothing().when(skillValidator).ensureSkillExists(true, SKILL_ID);
        doNothing().when(skillValidator).validateUserDoesNotHaveSkill(false, SKILL_ID, USER_ID);
        doNothing().when(skillValidator).validateEnoughSkillOffers(offers);

        skillService.acquireSkillFromOffers(SKILL_ID, USER_ID);

        verify(skillRepository, times(1)).assignSkillToUser(SKILL_ID, USER_ID);
        verify(userSkillGuaranteeMapper, times(1)).toUserSkillGuarantees(offers);
        verify(userSkillGuaranteeService, times(1)).saveAll(userSkillGuaranteeCaptor.capture());

        List<UserSkillGuarantee> capturedList = userSkillGuaranteeCaptor.getValue();

        assertEquals(1, capturedList.size());

        assertUserSkillGuaranteeMatchesOffer(capturedList.get(0), offer);
    }

    private void assertUserSkillGuaranteeMatchesOffer(UserSkillGuarantee guarantee, SkillOffer offer) {
        assertNotNull(guarantee.getUser());
        assertEquals(
                offer.getRecommendation().getReceiver().getId(),
                guarantee.getUser().getId()
        );

        assertNotNull(guarantee.getGuarantor());
        assertEquals(
                offer.getRecommendation().getAuthor().getId(),
                guarantee.getGuarantor().getId()
        );

        assertNotNull(guarantee.getSkill());
        assertEquals(
                offer.getSkill().getId(),
                guarantee.getSkill().getId()
        );
    }

    private CreateSkillDto createSkillDto() {
        return new CreateSkillDto(SKILL_TITLE);
    }

    private Skill createSkill() {
        return Skill.builder()
                .id(SKILL_ID)
                .title(SKILL_TITLE)
                .build();
    }

    private User createUser(long id) {
        return User.builder()
                .id(id)
                .build();
    }

    private UserDto createUserDto() {
        return new UserDto(GUARANTOR_ID, USER_NAME, EMAIL, PHONE, ABOUT_ME);
    }

    private Skill createSkillWithGuarantees(List<UserSkillGuarantee> guarantees) {
        return Skill.builder()
                .id(SKILL_ID)
                .title(SKILL_TITLE)
                .guarantees(guarantees)
                .build();
    }

    private UserSkillGuarantee createUserSkillGuarantee(User user, User guarantor) {
        return UserSkillGuarantee.builder()
                .user(user)
                .guarantor(guarantor)
                .build();
    }

    private Recommendation createRecommendation(User author) {
        return Recommendation.builder()
                .author(author)
                .build();
    }

    private SkillOffer createSkillOffer(Recommendation recommendation) {
        return SkillOffer.builder()
                .recommendation(recommendation)
                .build();
    }
}
