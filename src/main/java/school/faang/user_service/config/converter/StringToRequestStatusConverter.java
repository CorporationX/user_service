package school.faang.user_service.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.RequestStatus;

@Component
public class StringToRequestStatusConverter implements Converter<String, RequestStatus> {
  @Override
  public RequestStatus convert(String source) {
    return RequestStatus.valueOf(source.toUpperCase());
  }
}
