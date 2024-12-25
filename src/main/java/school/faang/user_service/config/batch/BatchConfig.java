package school.faang.user_service.config.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import school.faang.user_service.config.kafka.KafkaTopicsProps;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.message.event.reindex.user.UserDocument;
import school.faang.user_service.message.producer.KeyedMessagePublisher;
import school.faang.user_service.model.User;
import school.faang.user_service.repository.UserRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private static final String USER_REINDEX_STEP_NAME = "userReindexStep";
    private static final String THREAD_NAME_PREFIX_BATCH_USER = "user-batch-";

    private final JobsConfig jobsConfig;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KeyedMessagePublisher keyedMessagePublisher;
    private final KafkaTopicsProps kafkaTopicsProps;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserPartitioner userPartitioner;

    @Value("${thread-pool.user-batch-thread-pool.core-pool-size}")
    private int corePoolSize;

    @Value("${thread-pool.user-batch-thread-pool.max-pool-size}")
    private int maxPoolSize;

    @Value("${thread-pool.user-batch-thread-pool.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Bean
    public Job userReindexJob() {
        JobsConfig.JobProps userReindexJob = jobsConfig.getUserReindexingJob();
        return new JobBuilder(userReindexJob.getName(), jobRepository)
                .start(masterUserReindexStep())
                .build();
    }

    @Bean
    public Step masterUserReindexStep() {
        return new StepBuilder("masterStep", jobRepository)
                .partitioner(USER_REINDEX_STEP_NAME, userPartitioner)
                .step(workerUserReindexStep())
                .taskExecutor(taskExecutor())
                .gridSize(jobsConfig.getUserReindexingJob().getGridSize())
                .build();
    }

    @Bean
    public Step workerUserReindexStep() {
        JobsConfig.JobProps userReindexJob = jobsConfig.getUserReindexingJob();
        return new StepBuilder(USER_REINDEX_STEP_NAME, jobRepository)
                .<User, UserDocument>chunk(userReindexJob.getChunkSize(), transactionManager)
                .reader(userItemReader(null, null))
                .processor(userItemProcessor())
                .writer(userItemWriter())
                .build();
    }

    @StepScope
    @Bean
    public ItemReader<User> userItemReader(
            @Value("#{stepExecutionContext['minId']}") Long minId,
            @Value("#{stepExecutionContext['maxId']}") Long maxId
    ) {
        JobsConfig.JobProps userReindexJob = jobsConfig.getUserReindexingJob();
        JobsConfig.ReaderProps reader = userReindexJob.getReader();

        Map<String, Sort.Direction> sortMap = new LinkedHashMap<>();
        reader.getSorts().forEach(sortProps ->
                sortMap.put(sortProps.getField(), sortProps.getDirection()));

        return new RepositoryItemReaderBuilder<User>()
                .repository(userRepository)
                .methodName(reader.getMethodName())
                .arguments(minId, maxId)
                .sorts(sortMap)
                .name(reader.getName())
                .pageSize(userReindexJob.getChunkSize())
                .build();
    }

    @Bean
    public ItemProcessor<User, UserDocument> userItemProcessor() {
        return userMapper::toUserDocument;
    }

    @Bean
    public ItemWriter<UserDocument> userItemWriter() {
        KafkaTopicsProps.Topic userIndexingTopic = kafkaTopicsProps.getUserIndexingTopic();
        return items -> items.forEach(userDocument ->
                keyedMessagePublisher.send(
                        userIndexingTopic.getName(),
                        userDocument.getResourceId().toString(),
                        userDocument));
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix(THREAD_NAME_PREFIX_BATCH_USER);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setAwaitTerminationSeconds(awaitTerminationSeconds);
        return executor;
    }
}
