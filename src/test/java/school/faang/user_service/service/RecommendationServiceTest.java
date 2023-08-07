package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.mapper.skill.SkillMapper;
import school.faang.user_service.mapper.skill.UserSkillGuaranteeMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;
import school.faang.user_service.validator.SkillValidator;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private SkillValidator skillValidator;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private RecommendationMapper recommendationMapper;
    @Mock
    private SkillMapper skillMapper;
    @Mock
    private UserSkillGuaranteeMapper userSkillGuaranteeMapper;

    @Test
    void testCreate() {
        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        List<SkillOfferDto> skillOffers = Arrays.asList(skillOfferDto);
        RecommendationDto recommendationDto = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Test recommendation")
                .skillOffers(skillOffers)
                .build();
        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        Recommendation recommendationEntity = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Test recommendation")
                .skillOffers(new ArrayList<>())
                .build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L).build();

        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationDto);
        when(recommendationRepository.create(anyLong(), anyLong(), anyString())).thenReturn(1L);
        when(recommendationRepository.findById(anyLong())).thenReturn(Optional.of(recommendationEntity));
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);
        when(skillOfferRepository.findById(anyLong())).thenReturn(Optional.of(skillOffer));

        RecommendationDto result = recommendationService.create(recommendationDto);

        verify(recommendationValidator).validateRecommendationContent(recommendationDto);
        verify(recommendationValidator).validateRecommendationTerm(recommendationDto);
        verify(skillValidator).validateSkillOffersDto(recommendationDto);
        verify(recommendationRepository, times(1)).create(anyLong(), anyLong(), anyString());
        verify(recommendationRepository, times(1)).findById(anyLong());
        verify(skillOfferRepository, times(1)).create(anyLong(), anyLong());

        assertEquals(recommendationDto.getId(), result.getId());
        assertEquals(recommendationDto.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationDto.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationDto.getContent(), result.getContent());
        assertEquals(recommendationDto.getSkillOffers(), result.getSkillOffers());
    }

    @Test
    void testCreate_InvalidRecommendation_ThrowsDataValidationException() {

        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(1L)
                .receiverId(2L)
                .content("Invalid recommendation") // Content that fails validation
                .skillOffers(Collections.emptyList())
                .build();

        RecommendationService recommendationServiceSpy = spy(recommendationService);

        doThrow(DataValidationException.class)
                .when(recommendationValidator)
                .validateRecommendationContent(recommendationDto);

        assertThrows(DataValidationException.class, () -> recommendationServiceSpy.create(recommendationDto));

        verify(recommendationRepository, never()).create(anyLong(), anyLong(), anyString());
        verify(recommendationRepository, never()).findById(anyLong());

        verify(recommendationServiceSpy, never()).saveSkillOffers(any(Recommendation.class), anyList());

        verify(recommendationMapper, never()).toDto(any(Recommendation.class));
    }

    @Test
    void testCreate_RecommendationNotFound_ThrowsEntityNotFoundException() {

        long authorId = 1L;
        long receiverId = 2L;
        String content = "Test recommendation";

        RecommendationDto recommendationDto = RecommendationDto.builder()
                .authorId(authorId)
                .receiverId(receiverId)
                .content(content)
                .build();

        when(recommendationRepository.create(authorId, receiverId, content)).thenReturn(1L);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testCreate_InvalidRecommendation_ThrowsValidationException() {
        // Given
        long authorId = 1L;
        long receiverId = 2L;
        String content = "Test recommendation";

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setContent(content);

        // Simulate validation failure
        doThrow(DataValidationException.class).when(recommendationValidator)
                .validateRecommendationContent(recommendationDto);

        // When/Then
        assertThrows(DataValidationException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testCreate_SkillNotFound_ThrowsEntityNotFoundException() {
        long authorId = 1L;
        long receiverId = 2L;
        String content = "Test recommendation";

        RecommendationDto recommendationDto = new RecommendationDto();
        recommendationDto.setAuthorId(authorId);
        recommendationDto.setReceiverId(receiverId);
        recommendationDto.setContent(content);

        SkillOfferDto skillOfferDto = new SkillOfferDto();
        skillOfferDto.setId(1L);
        skillOfferDto.setSkill(3L);
        skillOfferDto.setRecommendation(1L);
        List<SkillOfferDto> skillOffers = Collections.singletonList(skillOfferDto);
        recommendationDto.setSkillOffers(skillOffers);
        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();

        when(recommendationRepository.create(authorId, receiverId, content)).thenReturn(1L);

        Recommendation recommendationEntity = new Recommendation();
        recommendationEntity.setId(1L);
        recommendationEntity.setAuthor(author);
        recommendationEntity.setReceiver(receiver);
        recommendationEntity.setContent(content);
        when(recommendationRepository.findById(1L)).thenReturn(Optional.of(recommendationEntity));

        assertThrows(EntityNotFoundException.class, () -> recommendationService.create(recommendationDto));
    }

    @Test
    void testUpdate() {
        SkillOfferDto skillOfferDtoFirst = SkillOfferDto.builder()
                .id(1L)
                .skill(1L)
                .recommendation(1L)
                .build();
        SkillOfferDto skillOfferDtoSecond = SkillOfferDto.builder()
                .id(2L)
                .skill(2L)
                .recommendation(2L)
                .build();

        List<SkillOfferDto> skillOffers = new ArrayList<>();
        skillOffers.add(skillOfferDtoFirst);
        skillOffers.add(skillOfferDtoSecond);

        RecommendationDto recommendationUpdate = RecommendationDto.builder()
                .id(1L)
                .authorId(1L)
                .receiverId(2L)
                .content("Updated recommendation")
                .skillOffers(skillOffers)
                .build();

        User author = User.builder().id(1L).build();
        User receiver = User.builder().id(2L).build();
        SkillOffer skillOffer = SkillOffer.builder()
                .id(1L).build();
        List<SkillOffer> skillOfferList = new ArrayList<>();
        skillOfferList.add(skillOffer);

        Recommendation recommendationEntity = Recommendation.builder()
                .id(1L)
                .author(author)
                .receiver(receiver)
                .content("Original recommendation")
                .skillOffers(skillOfferList)
                .build();

        when(recommendationRepository.update(recommendationUpdate.getAuthorId(),
                recommendationUpdate.getReceiverId(),
                recommendationUpdate.getContent())).
                thenReturn(recommendationEntity);
        when(skillOfferRepository.create(anyLong(), anyLong())).thenReturn(1L);
        when(skillOfferRepository.findById(anyLong())).thenReturn(Optional.of(skillOffer));
        when(recommendationMapper.toDto(any(Recommendation.class))).thenReturn(recommendationUpdate);

        RecommendationDto result = recommendationService.update(recommendationUpdate);

        verify(recommendationValidator).validateRecommendationContent(recommendationUpdate);
        verify(recommendationValidator).validateRecommendationTerm(recommendationUpdate);
        verify(skillValidator).validateSkillOffersDto(recommendationUpdate);
        verify(recommendationRepository, times(1)).update(anyLong(), anyLong(), anyString());
        verify(skillOfferRepository, times(1))
                .deleteAllByRecommendationId(recommendationEntity.getId());
        verify(skillOfferRepository, times(1)).create(1, 1);

        assertEquals(recommendationUpdate.getId(), result.getId());
        assertEquals(recommendationUpdate.getAuthorId(), result.getAuthorId());
        assertEquals(recommendationUpdate.getReceiverId(), result.getReceiverId());
        assertEquals(recommendationUpdate.getContent(), result.getContent());
        assertEquals(recommendationUpdate.getSkillOffers(), result.getSkillOffers());
    }

    @Test
    void testValidate_CallsValidators() {
        RecommendationDto recommendationDto = new RecommendationDto();

        recommendationService.validate(recommendationDto);

        verify(recommendationValidator, times(1)).validateRecommendationContent(recommendationDto);

        verify(recommendationValidator, times(1)).validateRecommendationTerm(recommendationDto);

        verify(skillValidator, times(1)).validateSkillOffersDto(recommendationDto);
    }

    @Test
    void testDelete_InvokesDeleteById() {
        recommendationService.delete(1);
        verify(recommendationRepository).deleteById(1L);
    }

    @Test
    void testGetAllUserRecommendations_ReceiverExists_ReturnRecommendationDtos() {
        long receiverId = 1L;

        User receiver = new User();
        receiver.setId(receiverId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(101L);
        recommendation1.setReceiver(receiver);
        recommendation1.setContent("Recommendation 1");

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(102L);
        recommendation2.setReceiver(receiver);
        recommendation2.setContent("Recommendation 2");

        List<Recommendation> recommendations = List.of(recommendation1, recommendation2);

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(101L);
        recommendationDto1.setReceiverId(receiverId);
        recommendationDto1.setContent("Recommendation 1");

        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(102L);
        recommendationDto2.setReceiverId(receiverId);
        recommendationDto2.setContent("Recommendation 2");

        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(recommendationRepository.findAllByReceiverId(receiverId)).thenReturn(Optional.of(recommendations));
        when(recommendationMapper.toRecommendationDtos(recommendations))
                .thenReturn(List.of(recommendationDto1, recommendationDto2));

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(receiverId, result.get(0).getReceiverId());
        assertEquals("Recommendation 1", result.get(0).getContent());

        assertEquals(102L, result.get(1).getId());
        assertEquals(receiverId, result.get(1).getReceiverId());
        assertEquals("Recommendation 2", result.get(1).getContent());
    }

    @Test
    void testGetAllUserRecommendations_ReceiverNotExists_ThrowEntityNotFoundException() {
        long receiverId = 1L;

        when(userRepository.findById(receiverId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.getAllUserRecommendations(receiverId));
    }

    @Test
    void testGetAllGivenRecommendations_AuthorExists_ReturnRecommendationDtos() {
        long authorId = 1L;

        User author = new User();
        author.setId(authorId);

        Recommendation recommendation1 = new Recommendation();
        recommendation1.setId(101L);
        recommendation1.setAuthor(author);
        recommendation1.setContent("Recommendation 1");

        Recommendation recommendation2 = new Recommendation();
        recommendation2.setId(102L);
        recommendation2.setAuthor(author);
        recommendation2.setContent("Recommendation 2");

        List<Recommendation> recommendations = List.of(recommendation1, recommendation2);

        RecommendationDto recommendationDto1 = new RecommendationDto();
        recommendationDto1.setId(101L);
        recommendationDto1.setAuthorId(authorId);
        recommendationDto1.setContent("Recommendation 1");

        RecommendationDto recommendationDto2 = new RecommendationDto();
        recommendationDto2.setId(102L);
        recommendationDto2.setAuthorId(authorId);
        recommendationDto2.setContent("Recommendation 2");

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(recommendationRepository.findAllByAuthorId(authorId)).thenReturn(Optional.of(recommendations));
        when(recommendationMapper.toRecommendationDtos(recommendations)).thenReturn(List.of(recommendationDto1, recommendationDto2));

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertEquals(2, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(authorId, result.get(0).getAuthorId());
        assertEquals("Recommendation 1", result.get(0).getContent());

        assertEquals(102L, result.get(1).getId());
        assertEquals(authorId, result.get(1).getAuthorId());
        assertEquals("Recommendation 2", result.get(1).getContent());
    }

    @Test
    void testGetAllGivenRecommendations_AuthorNotExists_ThrowEntityNotFoundException() {
        long authorId = 1L;

        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.getAllGivenRecommendations(authorId));
    }

    @Test
    void testGetAllGivenRecommendations_NoRecommendations_ReturnEmptyList() {
        long authorId = 1L;

        User author = new User();
        author.setId(authorId);

        List<Recommendation> recommendations = Collections.emptyList();

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(recommendationRepository.findAllByAuthorId(authorId)).thenReturn(Optional.of(recommendations));
        when(recommendationMapper.toRecommendationDtos(recommendations)).thenReturn(Collections.emptyList());

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllGivenRecommendations_AuthorExistsButNoRecommendations_ReturnEmptyList() {
        long authorId = 1L;

        User author = new User();
        author.setId(authorId);

        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));
        when(recommendationRepository.findAllByAuthorId(authorId)).thenReturn(Optional.empty());

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllGivenRecommendations_NonExistingAuthor_ThrowsEntityNotFoundException() {
        long authorId = 1L;

        when(userRepository.findById(authorId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> recommendationService.getAllGivenRecommendations(authorId));

        verify(userRepository).findById(authorId);

        verify(recommendationRepository, never()).findAllByAuthorId(anyLong());
    }

    @Test
    void testGetUserSkillsAndConvertToDtos_NoSkills_ReturnsEmptyList() {
        long userId = 1L;

        when(skillRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<SkillDto> skillDtos = recommendationService.getUserSkillsAndConvertToDtos(userId);

        assertEquals(0, skillDtos.size());
        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, never()).toDto(any(Skill.class));
    }

    @Test
    void testGetUserSkillsAndConvertToDtos_SkillsExist_ReturnsSkillDtos() {
        long userId = 1L;

        List<Skill> skills = new ArrayList<>();
        Skill skill1 = new Skill();
        skill1.setId(101L);
        skill1.setTitle("Skill 1");
        skills.add(skill1);

        Skill skill2 = new Skill();
        skill2.setId(102L);
        skill2.setTitle("Skill 2");
        skills.add(skill2);

        SkillDto skillDto1 = new SkillDto();
        skillDto1.setId(101L);
        skillDto1.setTitle("Skill 1");

        SkillDto skillDto2 = new SkillDto();
        skillDto2.setId(102L);
        skillDto2.setTitle("Skill 2");

        when(skillRepository.findAllByUserId(userId)).thenReturn(skills);

        when(skillMapper.toDto(skill1)).thenReturn(skillDto1);
        when(skillMapper.toDto(skill2)).thenReturn(skillDto2);

        List<SkillDto> skillDtos = recommendationService.getUserSkillsAndConvertToDtos(userId);

        assertEquals(2, skillDtos.size());
        assertEquals(skillDto1, skillDtos.get(0));
        assertEquals(skillDto2, skillDtos.get(1));

        verify(skillRepository, times(1)).findAllByUserId(userId);
        verify(skillMapper, times(1)).toDto(skill1);
        verify(skillMapper, times(1)).toDto(skill2);
    }
}








