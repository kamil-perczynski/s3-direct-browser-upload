package perczynski.kamil.s3uploads.s3;

import com.amazonaws.auth.AWSCredentials;
import lombok.Builder;
import lombok.Value;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Value
@Builder
public class UploadPolicy {

    public static final String HMAC_SHA256 = "AWS4-HMAC-SHA256";

    /**
     * AWS date format converter.
     *
     * @implNote Implementation taken directly from Node JS SDK
     */
    public static String asAwsDate(Instant instant) {
        return instant.toString()
                .replaceAll("[:\\-]|\\.\\d{3}", "");
    }

    /**
     * AWS short date format converter.
     *
     * @implNote Implementation taken directly from Node JS SDK
     */
    public static String asAwsShortDate(Instant instant) {
        return asAwsDate(instant).substring(0, 8);
    }

    public static UploadPolicyBuilder preconfigure(AWSCredentials credentials, String region) {
        return builder()
                .accessKeyId(credentials.getAWSAccessKeyId())
                .region(region)
                .expiration(Duration.of(1L, ChronoUnit.HOURS));
    }

    public static AwsPolicy asAwsPolicy(UploadPolicy policy) {
        String credentialsId = credentialsId(policy);
        List<Map.Entry<String, String>> conditions = List.of(
                Map.entry("key", policy.getKey()),
                Map.entry("bucket", policy.getBucket()),
                Map.entry("X-Amz-Algorithm", HMAC_SHA256),
                Map.entry("X-Amz-Credential", credentialsId),
                Map.entry("X-Amz-Date", asAwsDate(policy.getDate()))
        );

        Instant expirationTime = policy.getDate().plus(policy.getExpiration());
        return new AwsPolicy(expirationTime, conditions);
    }

    Duration expiration;
    String key;
    String bucket;
    String accessKeyId;
    String region;
    Instant date;

    private static String credentialsId(UploadPolicy policy) {
        return String.format(
                "%s/%s/%s/%s/%s",
                policy.getAccessKeyId(),
                asAwsShortDate(policy.getDate()),
                policy.getRegion(),
                "s3",
                "aws4_request"
        );
    }

    @Value
    static class AwsPolicy {
        Instant expiration;
        List<Map.Entry<String, String>> conditions;
    }

}
