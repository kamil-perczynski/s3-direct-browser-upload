package perczynski.kamil.s3uploads.web;

import lombok.AllArgsConstructor;
import perczynski.kamil.s3uploads.s3.S3Controller;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@AllArgsConstructor
public class WebHandler extends AbstractHandler {

    private final S3Controller s3Controller;

    public void handle(String target,
                       Request request,
                       HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException {
        request.setHandled(true);
        try {
            HttpResponse response = "/s3/url".equals(target)
                    ? s3Controller.generatePresignedPost()
                    : s3Controller.renderForm();
            httpServletResponse.setStatus(response.getStatus());
            for (Map.Entry<String, String> entry : response.getHeaders().entrySet()) {
                httpServletResponse.setHeader(entry.getKey(), entry.getValue());
            }
            response.getBody().write(httpServletResponse.getOutputStream());
        } catch (Exception e) {
            httpServletResponse.sendError(500);
            e.printStackTrace(httpServletResponse.getWriter());
        }
    }
}
