package perczynski.kamil.s3uploads.web;

import com.github.jknack.handlebars.Handlebars;
import lombok.Value;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

@Value
public class HandlebarsBody implements HttpResponse.Writable {

    String templateLocation;
    Object model;
    Handlebars handlebars;

    @Override
    public void write(ServletOutputStream stream) throws IOException {
        String html = handlebars.compile(templateLocation).apply(model);
        stream.print(html);
    }
}
