package school.faang.user_service.controller.user;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
//    private ObjectMapper objectMapper;
//    private MockMvc mockMvc;
//    private UserDto userDto;
//    private UserDto anotherUserDto;
//    private List<Long> userIds;
//    private UserFilterDto userFilterDto;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private UserContext userContext;
//
//    @InjectMocks
//    private UserController userController;
//
//    @BeforeEach
//    public void setUp() {
//        userDto = new UserDto();
//        userDto.setId(2L);
//        anotherUserDto = new UserDto();
//        anotherUserDto.setId(3L);
//
//        userFilterDto = new UserFilterDto();
//
//        objectMapper = new ObjectMapper();
//
//        mockMvc = MockMvcBuilders.standaloneSetup(userController)
//                .setControllerAdvice(new GlobalRestExceptionHandler())
//                .build();
//    }

//    @Test
//    @DisplayName("Should return a certain user for a given existing ID")
//    public void testGetUser_Success() throws Exception {
//        long validId = 1L;
//
//        when(userService.getUser(validId)).thenReturn(userDto);
//
//        mockMvc.perform(get("/users/{userId}", validId))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
//
//        verify(userService).getUser(validId);
//    }
//
//    @Test
//    @DisplayName("Should throw EntityNotFoundException when user is not found by ID")
//    public void testGetUser_NonExistingId() throws Exception {
//        long invalidId = 100500L;
//
//        when(userService.getUser(invalidId)).thenAnswer(invocation -> {
//            Long id = invocation.getArgument(0);
//            throw new EntityNotFoundException("User not found with id: " + id);
//        });
//
//        mockMvc.perform(get("/users/{userId}", invalidId))
//                .andExpect(status().isNotFound())
//                .andExpect(result -> assertInstanceOf(EntityNotFoundException.class, result.getResolvedException()))
//                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException())
//                        .getMessage().contains("User not found with id: 100500")));
//
//        verify(userService).getUser(invalidId);
//    }
//
//    @Test
//    @DisplayName("Should return users for given list of IDs")
//    public void testGetUsersByIds_Success() throws Exception {
//        List<UserDto> dtos = Arrays.asList(userDto, anotherUserDto);
//        userIds = Arrays.asList(2L, 3L);
//
//        when(userService.getUsersByIds(userIds)).thenReturn(dtos);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userIds)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
//                .andExpect(content().json(objectMapper.writeValueAsString(dtos)));
//
//        verify(userService).getUsersByIds(userIds);
//    }
//
//    @Test
//    @DisplayName("Should return empty list when no users are found for given IDs")
//    void testGetUsersByIds_NotFound() throws Exception {
//        userIds = List.of(100L, 200L, 300L);
//
//        when(userService.getUsersByIds(userIds)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(userIds)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$").isEmpty());
//
//        verify(userService).getUsersByIds(userIds);
//    }
//
//    @Test
//    @DisplayName("Should return filtered users successfully with correct user ID and filters")
//    public void testGetFilteredUsers_Success() throws Exception {
//        Long userId = 1L;
//        String requestBody = objectMapper.writeValueAsString(userFilterDto);
//
//        when(userContext.getUserId()).thenReturn(userId);
//        when(userService.getFilteredUsers(userFilterDto, userId)).thenReturn(List.of(userDto, anotherUserDto));
//
//        mockMvc.perform(get("/users/filtered")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isArray())
//                .andExpect(jsonPath("$[0].id").value(2L))
//                .andExpect(jsonPath("$[1].id").value(3L));
//    }
//
//    @Test
//    @DisplayName("Should return 400 Bad Request when invalid json is provided")
//    public void testGetFilteredUsers_InvalidRequest() throws Exception {
//        String invalidJson = "{invalid json}";
//
//        mockMvc.perform(get("/users/filtered")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(invalidJson))
//                .andExpect(status().isBadRequest());
//    }
}
