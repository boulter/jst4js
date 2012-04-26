package jst;

import java.io.InputStream;
import java.io.IOException;

public interface TemplateLoader {

    public InputStream load( String url ) throws IOException;

    public boolean shouldRefresh( String url, long compiledTimestamp );

    public String getRootUrl();
    
}
