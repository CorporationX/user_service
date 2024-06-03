package school.faang.user_service.controller.user;

import com.json.student.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.config.context.UserContext;
import school.faang.user_service.config.context.UserHeaderFilter;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.csv.CSVFileService;
import school.faang.user_service.service.csv.CsvFileConverter;
import school.faang.user_service.service.user.UserService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private CSVFileService csvFileService;

    @MockBean
    private CsvFileConverter converter;

    private User user;
    private UserDto userDto;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UserHeaderFilter userHeaderFilter;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1L);

        userDto = new UserDto();
        userDto.setId(1L);
    }

    @Test
    public void testConvertCsvFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "name,\nJohn".getBytes());
        List<Person> persons = Collections.singletonList(new Person());

        when(converter.convertCsvToPerson(any(MultipartFile.class))).thenReturn(persons);
        doNothing().when(csvFileService).convertCsvFile(anyList());

        mockMvc.perform(multipart("/users/add/file").file(file))
                .andExpect(status().isOk());
    }
}
