package perczynski.kamil.s3uploads.s3;

import com.amazonaws.auth.AWSCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import perczynski.kamil.s3uploads.s3.UploadPolicy.AwsPolicy;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class S3Service {

    private final String bucketName;
    private final String region;
    private final AWSCredentials awsCredentials;
    private final ObjectMapper objectMapper;

    /**
     * Method mimics behavior of "createPresignedPost" operation from Node.js S3 SDK.
     *
     * @link https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-UsingHTTPPOST.html
     * @link https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-post-example.html
     * @link https://docs.aws.amazon.com/AmazonS3/latest/API/sigv4-HTTPPOSTForms.html
     * @link https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/S3.html#createPresignedPost-property
     */
    public PresignedUrl presignUrl() {
        Instant instant = Instant.now();
        String endpointUrl = String.format("https://s3.%s.amazonaws.com/%s", region, bucketName);

        UploadPolicy policy = UploadPolicy.preconfigure(awsCredentials, region)
                .bucket(bucketName)
                .key(UUID.randomUUID().toString())
                .date(instant)
                .build();
        AwsPolicy awsPolicy = UploadPolicy.asAwsPolicy(policy);
        String signature = computeSignature(awsPolicy, policy, awsCredentials);

        Map<String, String> fields = Stream.concat(
                awsPolicy.getConditions().stream(),
                Stream.of(
                        Map.entry("Policy", encodePolicy(awsPolicy)),
                        Map.entry("X-Amz-Signature", signature)
                )
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new PresignedUrl(endpointUrl, fields);
    }

    /**
     * Method for generating policy signature V4 for direct browser upload.
     *
     * @link https://docs.aws.amazon.com/AmazonS3/latest/API/sig-v4-authenticating-requests.html
     */
    private String computeSignature(AwsPolicy awsPolicy,
                                    UploadPolicy policy,
                                    AWSCredentials credentials) {
        String encodedPolicy = encodePolicy(awsPolicy);
        String shortDate = UploadPolicy.asAwsShortDate(policy.getDate());

        byte[] dateKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, "AWS4" + credentials.getAWSSecretKey())
                .hmac(shortDate);
        byte[] dateRegionKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateKey).hmac(policy.getRegion());
        byte[] dateRegionServiceKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateRegionKey).hmac("s3");
        byte[] signingKey = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, dateRegionServiceKey).hmac("aws4_request");

        return new HmacUtils(HmacAlgorithms.HMAC_SHA_256, signingKey).hmacHex(encodedPolicy);
    }

    @SneakyThrows
    private String encodePolicy(AwsPolicy policy) {
        String policyJson = objectMapper.writeValueAsString(policy);
        return Base64.getEncoder().encodeToString(policyJson.getBytes(StandardCharsets.UTF_8));
    }

}
