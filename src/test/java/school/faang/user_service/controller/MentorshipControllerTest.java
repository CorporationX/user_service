package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import school.faang.user_service.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class MenteeMapperTest {
    private MenteeMapper menteeMapper;

    @BeforeEach
    public void setUp() {
        menteeMapper = new MenteeMapperClass();
    }

    @ParameterizedTest
    @CsvSource({"one, email, 12345567"})
    public void testCorrectAnswer(String username, String email, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        MenteeDto menteeDto = menteeMapper.toUser(user);
        assertEquals(menteeDto.getUsername(), user.getUsername());
        assertEquals(menteeDto.getEmail(), user.getEmail());
        assertEquals(menteeDto.getPhone(), user.getPhone());
    }
}