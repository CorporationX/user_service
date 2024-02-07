package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.student.Person;
import school.faang.user_service.mapper.UserPersonMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Component
public class CsvToPersonParser {
    private static final int THREAD_NUM = 5;
    private static final long MAX_FILE_CHUNK_SIZE = 13_000L;
    private static final long EXPECTED_CHUNK_SIZE = 1_300L;
    private final CsvMapper csvPersonMapper;
    private final UserPersonMapper userPersonMapper;
    private final Lock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);

    public List<User> parseUsers(MultipartFile csvFile) throws IOException {
        List<User> users = new ArrayList<>();
        InputStream inputStream = BOMInputStream.builder()
                .setInputStream(csvFile.getInputStream())
                .setByteOrderMarks(ByteOrderMark.UTF_8)
                .setInclude(false)
                .get();

        if (csvFile.getSize() > MAX_FILE_CHUNK_SIZE) {
            parallelParse(inputStream, users);
        } else {
            linealParse(inputStream, users);
        }
        inputStream.close();
        return users;
    }

    private void parallelParse(InputStream inputStream, List<User> users) throws IOException {
        List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        StringBuilder csvStrBuilderFile = new StringBuilder();

        String line;
        int count = 0;
        String header = reader.readLine();
        while ((line = reader.readLine()) != null) {
            if (count == 0) {
                csvStrBuilderFile.append(header).append("\n");
            } else {
                csvStrBuilderFile.append(line).append("\n");
            }
            count++;
            if (count == MAX_FILE_CHUNK_SIZE/EXPECTED_CHUNK_SIZE) {
                runAsync(users, completableFutures, csvStrBuilderFile);
                count = 0;
                csvStrBuilderFile = new StringBuilder();
            }
        }
        if (count > 0) {
            runAsync(users, completableFutures, csvStrBuilderFile);
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();

        executor.shutdown();
        inputStreamReader.close();
        reader.close();
    }

    private void runAsync(List<User> users, List<CompletableFuture<Void>> completableFutures, StringBuilder csvStrBuilderFile) {
        completableFutures.add(CompletableFuture.runAsync(
                () -> {
                    try {
                        Thread.sleep(5000);
                        parseCsvToUser(csvStrBuilderFile.toString().getBytes(), users);
                        System.out.println(Thread.currentThread());
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                , executor));
    }

    private void linealParse(InputStream inputStream, List<User> users) throws IOException {
        parseCsvToUser(inputStream.readAllBytes(), users);
    }

    private void parseCsvToUser(byte[] bytes, List<User> users) throws IOException {
        MappingIterator<Person> mappingIterator = csvPersonMapper
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(bytes);
        while (mappingIterator.hasNextValue()) {
            Person person = mappingIterator.nextValue();
            User user = userPersonMapper.toUser(person);
            user.setPassword(user.getEmail());
            lock.lock();
            try {
                users.add(user);
            } finally {
                lock.unlock();
            }
        }

        mappingIterator.close();
    }
}