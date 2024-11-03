package hadoop.learning.project.log.processor.model;

public record Resource(String httpVerb, String resourcePath, String protocol) {

    public static Resource mapToResource(String resourceString) {
        String[] resource = resourceString.split(" ");
        String httpVerb = resource[0];
        String resourcePath = resource[1];
        String protocol = resource[2];
        return new Resource(httpVerb, resourcePath, protocol);
    }
}