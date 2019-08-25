package perczynski.kamil.s3uploads.s3;

import lombok.Value;

import java.util.Map;

@Value
public class PresignedUrl {

    String url;
    Map<String, String> fields;

}
