package perczynski.kamil.s3uploads;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import perczynski.kamil.s3uploads.s3.S3Controller;
import perczynski.kamil.s3uploads.s3.S3Service;
import org.eclipse.jetty.server.Server;
import perczynski.kamil.s3uploads.web.WebHandler;

public class UploadsApplication {

    private static final String BUCKET_NAME = "BUCKET_NAME";

    public static void main(String[] args) throws Exception {
        String bucketName = System.getenv(BUCKET_NAME);
        if (bucketName == null || bucketName.trim().isEmpty()) {
            throw new IllegalStateException("You must configure bucket name first. Use environment variable BUCKET_NAME");
        }

        ObjectMapper objectMapper = objectMapper();
        S3Service s3Service = new S3Service(
                bucketName,
                new DefaultAwsRegionProviderChain().getRegion(),
                new DefaultAWSCredentialsProviderChain().getCredentials(),
                objectMapper
        );

        Server server = new Server(5000);
        server.setHandler(
                new WebHandler(
                        new S3Controller(
                                s3Service,
                                objectMapper,
                                handlebars()
                        )
                )
        );
        server.start();
        server.join();
    }

    private static Handlebars handlebars() {
        Handlebars handlebars = new Handlebars(new ClassPathTemplateLoader());
        handlebars.with(EscapingStrategy.HBS3);
        return handlebars;
    }

    private static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
