package school.faang.user_service.service;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

//import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;


import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRepository;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MentorshipServiceTest {
    @Mock
    private MentorshipRepository mentorshipRepository;
    @InjectMocks
    private MentorshipService mentorshipService;

    User user;

    @BeforeEach
    public void init() {
        user = User.builder()
                .id(1L)
                .mentors()
                .build();

    }

    /*@Test
    public void testListMentorsIsReturn() {
        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(user));
        User resultUser = mentorshipRepository.findById(1L).orElse(null);

        assertNotNull(resultUser);
        assertThat(new HashSet<>(user.getMentors()), is(new HashSet<>(resultUser.getMentors())));
    }*/

    /*@Test
    public void testNullUser() {
        Assert.assertThrows(IllegalArgumentException.class, () -> {
            mentorshipService.deleteMentor(500, 2);
            mentorshipService.deleteMentee(500, 2);

        })
    }*/
    @Test
    public void testDeleteMentor() {
        Long userId = 1L;
        List<User> listmentor = getList();

        when(mentorshipRepository.findById(1L)).thenReturn(Optional.of(user));
        mentorshipService.deleteMentor(3L, 1L);
    }

    @NotNull
    private static List<User> getList() {
        return new ArrayList<>(List.of(User.builder().id(5L).build(),
                User.builder().id(6L).build()));
    }
}
