package refresh.acci.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import refresh.acci.global.exception.CustomException;
import refresh.acci.global.exception.ErrorCode;

import java.util.Base64;

@Slf4j
public class SerializationUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object object) {
        try {
            String json = objectMapper.writeValueAsString(object);
            return Base64.getUrlEncoder().encodeToString(json.getBytes());
        } catch (JsonProcessingException e) {
            log.error("OAuth 요청 직렬화 실패: {}", object.getClass().getName(), e);
            throw new CustomException(ErrorCode.OAUTH_SERIALIZATION_FAILED);
        }
    }

    public static <T> T deserialize(String value, Class<T> cls) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value);
            String json = new String(bytes);
            return objectMapper.readValue(json, cls);
        } catch (Exception e) {
            log.error("OAuth 요청 역직렬화 실패: {}", cls.getName(), e);
            throw new CustomException(ErrorCode.OAUTH_DESERIALIZATION_FAILED);
        }
    }
}