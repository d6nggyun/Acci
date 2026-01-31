package refresh.acci.domain.file.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class S3FileService {

    private final S3Client s3Client;

    public void uploadFile(String bucket, String key, Path filePath) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build(),
                filePath
        );
    }

    public byte[] downloadFile(String bucket, String key) {
        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
                ).asByteArray();
    }
}
