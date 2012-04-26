package jst;

import java.io.InputStream;
import java.io.IOException;

public class ResourceTemplateLoader implements TemplateLoader {

    private String basePath;

    public ResourceTemplateLoader() {
    }

    public ResourceTemplateLoader(String basePath) {
        this.basePath = basePath;
    }

    public InputStream load(String url) throws IOException {
        return getClass().getResourceAsStream( "/" + (basePath != null ? basePath + "/" + url : url) ); 
    }

    public boolean shouldRefresh(String url, long compiledTimestamp) {
        return false;
    }

    public String getRootUrl() {
        return basePath;
    }
}
