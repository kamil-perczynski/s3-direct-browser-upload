package perczynski.kamil.s3uploads.s3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import lombok.AllArgsConstructor;
import perczynski.kamil.s3uploads.web.HandlebarsBody;
import perczynski.kamil.s3uploads.web.HttpResponse;
import perczynski.kamil.s3uploads.web.JsonBody;

import java.util.Map;

@AllArgsConstructor
public class S3Controller {

    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final Handlebars handlebars;

    public HttpResponse renderForm() {
        PresignedUrl url = s3Service.presignUrl();
        return HttpResponse.builder()
                .status(200)
                .headers(Map.of("Content-Type", "text/html"))
                .body(new HandlebarsBody("form", url, handlebars))
                .build();
    }

    public HttpResponse generatePresignedPost() {
        PresignedUrl url = s3Service.presignUrl();
        return HttpResponse.builder()
                .status(200)
                .headers(Map.of("Content-Type", "application/json"))
                .body(new JsonBody(url, objectMapper))
                .build();
    }

}
