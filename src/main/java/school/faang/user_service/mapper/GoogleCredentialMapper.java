package school.faang.user_service.mapper;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.GoogleCredential;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoogleCredentialMapper {
    GoogleClientSecrets.Details toDetails(GoogleCredential googleCredential);
}
