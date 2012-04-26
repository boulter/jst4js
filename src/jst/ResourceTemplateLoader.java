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

      InputStream stream = getClass().getResourceAsStream( "/" + (basePath != null ? basePath + "/" + url : url) ); 

      if (stream != null)
        return stream;
      
      return getClass().getResourceAsStream( "/" + (basePath != null ? basePath + "/" + url : url) + ".jst"); 
    }

    public boolean shouldRefresh(String url, long compiledTimestamp) {
        return false;
    }

    public String getRootUrl() {
        return basePath;
    }
}
