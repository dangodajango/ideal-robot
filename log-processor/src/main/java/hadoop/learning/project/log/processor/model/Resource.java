package hadoop.learning.project.log.processor.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
@Getter
public class Resource {

    private final String httpVerb;
    private final String resourcePath;
    private final String protocol;

    public static Resource mapToResource(String resourceString) {
        resourceString = resourceString.replace("\"", "");
        String[] resource = resourceString.split(" ");
        String httpVerb = resource[0];
        String resourcePath = resource[1];
        String protocol = resource[2];
        return new Resource(httpVerb, resourcePath, protocol);
    }
}