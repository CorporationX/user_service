package school.faang.user_service.service.user.upload;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.user.FileUploadedException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.user.UserMapperUtil;
import school.faang.user_service.pojo.Person;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvLoaderTest {

    @Mock
    private ObjectReader objectReader;

    @Mock
    private MappingIterator<Person> mappingIterator;

    @Mock
    private UserMapperUtil userMapperUtil;

    @Spy
    @InjectMocks
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @InjectMocks
    private CsvLoader csvLoader;

    MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        multipartFile = mock(MultipartFile.class);
    }

    @Test
    @DisplayName("Successfully parse CSV to users")
    public void testParseCsvToUsersSuccess() throws Exception {
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(objectReader.readValues(inputStream)).thenAnswer(invocation -> mappingIterator);

        Person john = new Person();
        john.setFirstName("John");
        john.setLastName("Doe");
        Person jane = new Person();
        jane.setFirstName("Jane");
        jane.setLastName("Doe");
        List<Person> persons = List.of(john, jane);

        when(userMapperUtil.getUserName(john)).thenReturn("John Doe");
        when(userMapperUtil.getUserName(jane)).thenReturn("Jane Doe");
        when(mappingIterator.readAll()).thenReturn(persons);

        User user = userMapper.toUserFromPerson(john);

        CompletableFuture<List<User>> futureUsers = csvLoader.parseCsvToUsers(multipartFile);

        List<User> users = futureUsers.join();

        assertEquals(user.getUsername(), john.getFirstName() + " " + john.getLastName());
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getUsername());
        assertEquals("Jane Doe", users.get(1).getUsername());

        verify(multipartFile).getInputStream();
        verify(objectReader).readValues(inputStream);
    }

    @Test
    @DisplayName("Fail to parse CSV with IOException")
    void testParseCsvToUsersWithIoException() throws Exception {
        when(multipartFile.getInputStream()).thenThrow(new IOException("File not found"));

        assertThrows(FileUploadedException.class, () -> csvLoader.parseCsvToUsers(multipartFile).join());

        verify(multipartFile).getInputStream();
        verify(objectReader, never()).readValues(any(InputStream.class));
    }
}
