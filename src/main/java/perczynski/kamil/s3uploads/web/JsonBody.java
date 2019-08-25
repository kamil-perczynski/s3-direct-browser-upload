package perczynski.kamil.s3uploads.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import perczynski.kamil.s3uploads.s3.PresignedUrl;
import lombok.Value;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Value
public class JsonBody implements HttpResponse.Writable {

    PresignedUrl presignedUrl;
    ObjectMapper objectMapper;

    @Override
    public void write(ServletOutputStream stream) throws IOException {
        objectMapper.writeValue(stream, presignedUrl);
    }
}
