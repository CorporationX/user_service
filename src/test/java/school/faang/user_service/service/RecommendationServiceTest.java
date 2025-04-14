package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import school.faang.user_service.dto.recomendation.RecommendationDto;
import school.faang.user_service.dto.recomendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.RecommendationValidator;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private RecommendationValidator recommendationValidator;
    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Mock
    private SkillOfferRepository skillOfferRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private RecommendationMapper recommendationMapper;
    @Captor
    private ArgumentCaptor<Long> skillIdCaptor;
    @Captor
    private ArgumentCaptor<Long> recommendationIdCaptor;

    static Stream<Arguments> skillOfferCases() {
        return Stream.of(
                Arguments.of(List.of(5L, 8L), 2),
                Arguments.of(List.of(10L, 20L, 30L), 3)
        );
    }

    private static Stream<Arguments> guaranteeCases() {
        User garanter = User.builder().id(1L).build();
        List<UserSkillGuarantee> guarantees = List.of(
                UserSkillGuarantee.builder().id(1L).guarantor(garanter).build()
        );
        Skill skillWithGuarantee = createSkill(100L, guarantees);
        Skill skillWithoutGuarantee = createSkill(200L, List.of());

        return Stream.of(
                Arguments.of(
                        List.of(skillWithGuarantee),
                        List.of(100L),
                        0
                ),
                Arguments.of(
                        List.of(skillWithoutGuarantee),
                        List.of(200L),
                        1
                )
        );
    }

    private static Stream<Arguments> recommendationCases() {
        Recommendation entity1 = Recommendation.builder().id(100L).build();
        RecommendationDto dto1 = RecommendationDto.builder().id(100L).build();

        Recommendation entity2 = Recommendation.builder().id(200L).build();
        RecommendationDto dto2 = RecommendationDto.builder().id(200L).build();

        return Stream.of(
                Arguments.of(List.of(entity1), List.of(dto1)),
                Arguments.of(List.of(entity1, entity2), List.of(dto1, dto2)),
                Arguments.of(List.of(), List.of())
        );
    }

    private static RecommendationDto createDto(Long authorId, Long receiverId, List<Long> skillIds) {
        List<SkillOfferDto> skillOffers = skillIds.stream()
                .map(id -> SkillOfferDto.builder().skillId(id).build())
                .toList();

        return RecommendationDto.builder()
                .authorId(authorId)
                .receiverId(receiverId)
                .skillOffers(skillOffers)
                .build();
    }

    private static Stream<Arguments> skillCases() {
        return Stream.of(
                Arguments.of(List.of(100L), 1),
                Arguments.of(List.of(200L, 300L), 2),
                Arguments.of(List.of(), 0)
        );
    }

    @ParameterizedTest
    @DisplayName("1.1-addSkillOffers-1")
    @MethodSource("skillOfferCases")
    void testAddSkillOffersSavesSkillsCorrectly(
            List<Long> inputSkillIds,
            int expectedSaveCalls
    ) {
        RecommendationDto dto = RecommendationDto.builder()
                .id(1L)
                .skillOffers(inputSkillIds.stream()
                        .map(skillId -> SkillOfferDto.builder().id(skillId.intValue()).skillId(skillId).title("Title").build())
                        .collect(Collectors.toList()))
                .build();

        recommendationService.addSkillOffers(dto);

        verify(skillOfferRepository, times(expectedSaveCalls))
                .create(skillIdCaptor.capture(), recommendationIdCaptor.capture());

        assertEquals(inputSkillIds, skillIdCaptor.getAllValues());
        assertTrue(recommendationIdCaptor.getAllValues().stream().allMatch(id -> id == 1L));
    }

    @ParameterizedTest
    @MethodSource("guaranteeCases")
    @DisplayName("1.1.getGuaranteedSkillIds-1")
    void getGuaranteedSkillIdsAddsNewGuaranteesWhenMissing(
            List<Skill> receiverSkills,
            List<Long> dtoSkillIds,
            int expectedNewGuarantees
    ) {
        RecommendationDto dto = createDto(1L, 2L, dtoSkillIds);
        User user2 = new User();
        user2.setId(2L);
        User user1 = new User();
        user1.setId(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(skillRepository.findAllByUserId(2L)).thenReturn(receiverSkills);

        Set<Long> receiverSkillIds = receiverSkills.stream().map(Skill::getId).collect(Collectors.toSet());
        int expectedUnmatchedSkillsCount = (int) dtoSkillIds.stream()
                .filter(id -> !receiverSkillIds.contains(id))
                .count();

        List<Long> result = recommendationService.getGuaranteedSkillIds(dto);

        verify(userSkillGuaranteeRepository, times(expectedNewGuarantees)).save(any());
        assertEquals(expectedUnmatchedSkillsCount, result.size(), "Incorrect number of unmatched skills");
    }

    @Test
    @DisplayName("1.1.getGuaranteedSkillIds-2")
    void getGuaranteedSkillIdsMissingUserThrowsException() {
        RecommendationDto dto = createDto(1L, 999L, List.of(100L));

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> recommendationService.getGuaranteedSkillIds(dto)
        );
    }

    @Test
    @DisplayName("1.1.getGuaranteedSkillIds-3")
    void getGuaranteedSkillIdsReturnsUnmatchedSkills() {
        Skill skill1 = createSkill(100L, List.of());
        Skill skill2 = createSkill(200L, List.of());
        RecommendationDto dto = createDto(1L, 2L, List.of(100L, 300L));
        User user = new User();
        user.setId(2L);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(skillRepository.findAllByUserId(2L)).thenReturn(List.of(skill1, skill2));

        List<Long> result = recommendationService.getGuaranteedSkillIds(dto);

        assertEquals(List.of(300L), result);
    }

    @ParameterizedTest
    @MethodSource("skillCases")
    @DisplayName("1.1-saveMismatchedSkill-1")
    void saveMismatchedSkillSavesGuaranteesWhenSkillsValid(
            List<Long> skillIds,
            int expectedSaves
    ) {
        User receiver = User.builder().id(2L).skills(new ArrayList<>()).build();
        User guarantor = User.builder().id(1L).skills(new ArrayList<>()).build();

        when(userRepository.findById(2L)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(1L)).thenReturn(Optional.of(guarantor));

        for (Long skillId : skillIds) {
            Skill skill = Skill.builder()
                    .id(skillId)
                    .guarantees(new ArrayList<>())
                    .build();
            when(skillRepository.findById(skillId)).thenReturn(Optional.of(skill));
        }

        recommendationService.saveMismatchedSkill(skillIds, createRecommendationDto(1L, 2L));

        verify(userSkillGuaranteeRepository, times(expectedSaves)).save(any());
        verify(skillRepository, times(expectedSaves)).save(any());
        verify(userRepository, times(expectedSaves != 0 ? 1 : 0)).save(receiver);
    }

    @Test
    @DisplayName("2")
    void updateValidDtoUpdatesAndReturnsDto() {
        User mockReceiver = User.builder().id(3L).build();
        User mockAuthor = User.builder().id(2L).build();

        RecommendationDto dto = RecommendationDto.builder()
                .id(1L)
                .authorId(mockAuthor.getId())
                .receiverId(mockReceiver.getId())
                .content("Valid content")
                .skillOffers(List.of(
                        SkillOfferDto.builder().id(100L).build(),
                        SkillOfferDto.builder().id(200L).build()
                ))
                .build();

        when(userRepository.findById(dto.getReceiverId()))
                .thenReturn(Optional.of(mockReceiver));
        when(userRepository.findById(dto.getAuthorId()))
                .thenReturn(Optional.of(mockAuthor));

        when(recommendationValidator.textAvailability(eq(dto))).thenReturn(true);
        doNothing().when(recommendationValidator).checkRecommendationInterval(eq(dto));
        when(recommendationValidator.checkingSkills(eq(dto))).thenReturn(true);

        doNothing().when(recommendationRepository)
                .update(eq(dto.getAuthorId()), eq(dto.getReceiverId()), eq(dto.getContent()));
        doNothing().when(skillOfferRepository).deleteAllByRecommendationId(eq(dto.getId()));

        RecommendationDto result = recommendationService.update(dto);

        verify(userRepository).findById(eq(dto.getReceiverId()));
        verify(userRepository).findById(eq(dto.getAuthorId()));
        verify(recommendationValidator).textAvailability(eq(dto));
        verify(recommendationValidator).checkRecommendationInterval(eq(dto));
        verify(recommendationValidator).checkingSkills(eq(dto));
        verify(recommendationRepository).update(eq(dto.getAuthorId()), eq(dto.getReceiverId()), eq(dto.getContent()));
        verify(skillOfferRepository).deleteAllByRecommendationId(eq(dto.getId()));
        assertEquals(dto, result);
    }


    @Test
    @DisplayName("2-clearingSkills")
    void clearingSkillsDeletesOldAndCreatesNewSkillOffers() {
        Long recommendationId = 1L;
        Long receiverId = 2L;
        Long authorId = 3L;

        RecommendationDto recommendation = RecommendationDto.builder()
                .id(recommendationId)
                .receiverId(receiverId)
                .authorId(authorId)
                .skillOffers(List.of(
                        SkillOfferDto.builder().skillId(100L).build(),
                        SkillOfferDto.builder().skillId(200L).build()
                ))
                .build();

        User receiver = User.builder().id(receiverId).build();
        User author = User.builder().id(authorId).build();
        when(userRepository.findById(receiverId)).thenReturn(Optional.of(receiver));
        when(userRepository.findById(authorId)).thenReturn(Optional.of(author));

        recommendationService.clearingSkills(recommendation);

        verify(skillOfferRepository).deleteAllByRecommendationId(recommendationId);
        verify(skillOfferRepository, times(2)).create(anyLong(), eq(recommendationId));
    }

    @Test
    @DisplayName("3.1")
    void deleteValidIdCallsRepositoryDelete() {
        long testId = 123L;
        recommendationService.delete(testId);
        verify(recommendationRepository).deleteById(testId);
    }

    @Test
    @DisplayName("3.2")
    void deleteNonExistentIdThrowsException() {
        long invalidId = 999L;
        doThrow(new EmptyResultDataAccessException(1))
                .when(recommendationRepository).deleteById(invalidId);

        assertThrows(
                EmptyResultDataAccessException.class,
                () -> recommendationService.delete(invalidId)
        );
    }

    @ParameterizedTest
    @DisplayName("4")
    @MethodSource("recommendationCases")
    void getAllUserRecommendations_ReturnsDtoList(
            List<Recommendation> mockEntities,
            List<RecommendationDto> expectedDtos
    ) {
        long receiverId = 1L;
        Page<Recommendation> page = new PageImpl<>(mockEntities);

        when(recommendationRepository.findAllByReceiverId(eq(receiverId), any(PageRequest.class)))
                .thenReturn(page);

        if (!mockEntities.isEmpty()) {
            for (int i = 0; i < mockEntities.size(); i++) {
                Recommendation entity = mockEntities.get(i);
                RecommendationDto dto = expectedDtos.get(i);
                lenient().when(recommendationMapper.toDto(entity)).thenReturn(dto);
            }
        }

        List<RecommendationDto> result = recommendationService.getAllUserRecommendations(receiverId);

        assertEquals(expectedDtos, result);
        verify(recommendationRepository).findAllByReceiverId(receiverId, PageRequest.of(0, 1));
    }

    @Test
    @DisplayName("4.1")
    void getAllUserRecommendationsVerifyPagination() {
        long receiverId = 1L;
        PageRequest expectedPageRequest = PageRequest.of(0, 1);

        when(recommendationRepository.findAllByReceiverId(receiverId, expectedPageRequest))
                .thenReturn(Page.empty());

        recommendationService.getAllUserRecommendations(receiverId);

        verify(recommendationRepository).findAllByReceiverId(receiverId, expectedPageRequest);
    }

    @Test
    @DisplayName("5.1")
    public void getAllGivenRecommendationsM5() {
        long authorId = 1L;
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<Recommendation> entities = List.of(
                createRecommendation(1L),
                createRecommendation(2L)
        );
        Page<Recommendation> page = new PageImpl<>(entities, pageRequest, entities.size());
        List<RecommendationDto> expectedDtos = List.of(
                createRecommendationDto(1L, 4L),
                createRecommendationDto(2L, 3L)
        );

        when(recommendationRepository.findAllByAuthorId(eq(authorId), eq(pageRequest))).thenReturn(page);
        when(recommendationMapper.toDto(any(Recommendation.class)))
                .thenAnswer(inv -> {
                    Recommendation entity = inv.getArgument(0);
                    return createRecommendationDto(entity.getId(), 1L);
                });

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertEquals(expectedDtos.size(), result.size());
        verify(recommendationRepository).findAllByAuthorId(authorId, pageRequest);
        verify(recommendationMapper, times(entities.size())).toDto(any(Recommendation.class));
    }

    @Test
    @DisplayName("5.3")
    void getAllGivenRecommendationsEmptyPageReturnsEmptyListM5() {
        long authorId = 1L;
        Page<Recommendation> emptyPage = Page.empty();

        when(recommendationRepository.findAllByAuthorId(eq(authorId), any()))
                .thenReturn(emptyPage);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertTrue(result.isEmpty());
        verify(recommendationMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("5.3")
    void getAllGivenRecommendationsVerifyPaginationM5() {
        long authorId = 1L;
        PageRequest expectedPageRequest = PageRequest.of(0, 1);

        when(recommendationRepository.findAllByAuthorId(eq(authorId), eq(expectedPageRequest)))
                .thenReturn(Page.empty());

        recommendationService.getAllGivenRecommendations(authorId);

        verify(recommendationRepository).findAllByAuthorId(authorId, expectedPageRequest);
    }

    @ParameterizedTest
    @DisplayName("5.4")
    @ValueSource(longs = {1L, 999L, 0L})
    void getAllGivenRecommendationsEmptyResultReturnsEmptyList(long authorId) {
        Page<Recommendation> page = new PageImpl<>(Collections.emptyList());

        when(recommendationRepository.findAllByAuthorId(eq(authorId), any(PageRequest.class))).thenReturn(page);

        List<RecommendationDto> result = recommendationService.getAllGivenRecommendations(authorId);

        assertTrue(result.isEmpty());
    }

    private Recommendation createRecommendation(Long id) {
        return Recommendation.builder()
                .id(id)
                .build();
    }

    private RecommendationDto createRecommendationDto(long id, long receiverId) {
        return RecommendationDto.builder().authorId(id).receiverId(receiverId).build();
    }

    private static Skill createSkill(long id, List<UserSkillGuarantee> guarantees) {
        return Skill.builder().id(id).guarantees(guarantees).build();
    }
}

