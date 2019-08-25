package perczynski.kamil.s3uploads.web;

import lombok.Builder;
import lombok.Value;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.Map;

@Value
@Builder
public class HttpResponse {

    int status;
    Map<String, String> headers;
    Writable body;

    public interface Writable {

        void write(ServletOutputStream stream) throws IOException;

    }
}
