package school.faang.user_service.service.user;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class CsvOfPeopleToUserParser {
    @Value("${person.parser.thread_num}")
    private int threadNum;
    @Value("${person.parser.max_file_chunk_size}")
    private long maxFileChunkSize;
    @Value("${person.parser.expected_chunk_size}")
    private long expectedChunkSize;
    private final CsvMapper csvPersonMapper;
    private final UserPersonMapper userPersonMapper;
    private final Lock lock = new ReentrantLock();
    private final ExecutorService executor = Executors.newFixedThreadPool(threadNum);

    public List<User> parse(MultipartFile csvFile) throws IOException {
        List<User> users = new ArrayList<>();
        InputStream inputStream = BOMInputStream.builder()
                .setInputStream(csvFile.getInputStream())
                .setByteOrderMarks(ByteOrderMark.UTF_8)
                .setInclude(false)
                .get();

        long csvFileSize = csvFile.getSize();
        if (csvFileSize > maxFileChunkSize) {
            log.debug("Csv file size is {}. parallel parse started", csvFileSize);
            parallelParse(inputStream, users);
        } else {
            log.debug("Csv file size is {}. linealParse parse started", csvFileSize);
            linealParse(inputStream, users);
        }
        inputStream.close();
        log.debug("Csv file parse finished. Parsed users count {}", users.size());
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
            if (count == maxFileChunkSize / expectedChunkSize) {
                log.debug("chunk with amount of lines {} of csv file is ready to parse", count);
                runAsync(users, completableFutures, csvStrBuilderFile);
                count = 0;
                csvStrBuilderFile = new StringBuilder();
            }
        }
        if (count > 0) {
            runAsync(users, completableFutures, csvStrBuilderFile);
        }

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
        log.debug("parallel parsing finished");

        executor.shutdown();
        inputStreamReader.close();
        reader.close();
    }

    private void runAsync(List<User> users, List<CompletableFuture<Void>> completableFutures, StringBuilder csvStrBuilderFile) {
        completableFutures.add(CompletableFuture.runAsync(
                () -> {
                    try {
                        parseCsvToPeopleAndMapToUser(csvStrBuilderFile.toString().getBytes(), users);
                        log.debug("started thread to parse chunk of csv file");
                    } catch (IOException e) {
                        log.debug("IOException trying to parallel parsing");
                        throw new RuntimeException(e);
                    }
                }
                , executor));
    }

    private void linealParse(InputStream inputStream, List<User> users) throws IOException {
        parseCsvToPeopleAndMapToUser(inputStream.readAllBytes(), users);
    }

    private void parseCsvToPeopleAndMapToUser(byte[] bytes, List<User> users) throws IOException {
        MappingIterator<Person> mappingIterator = csvPersonMapper
                .readerFor(Person.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(bytes);
        while (mappingIterator.hasNextValue()) {
            Person person = mappingIterator.nextValue();
            User user = userPersonMapper.toUser(person);
            user.setPassword(user.getEmail());
            log.debug("user with username {} parsed", user.getUsername());
            lock.lock();
            try {
                users.add(user);
                log.debug("user with username {} added", user.getUsername());
            } finally {
                lock.unlock();
            }
        }

        mappingIterator.close();
    }
}