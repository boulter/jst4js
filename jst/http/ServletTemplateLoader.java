package jst.http;

import jst.TemplateLoader;

import javax.servlet.ServletContext;
import java.io.*;

public class ServletTemplateLoader implements TemplateLoader {

    private ServletContext context;
    private String basePath;


    public ServletTemplateLoader(ServletContext context) {
        this.context = context;
    }

    public ServletTemplateLoader(ServletContext context, String basePath) {
        this(context);
        this.basePath = basePath;
    }

    public InputStream load(String url) throws IOException {
        File scriptFile = getScriptFile(url);
        if( scriptFile.exists() ) {
            return new BufferedInputStream( new FileInputStream( scriptFile ) );
        } else {
            return null;
        }
    }

    public boolean shouldRefresh(String url, long compiledTimestamp) {
        File scriptFile = getScriptFile(url);
        return compiledTimestamp < scriptFile.lastModified() ;
    }

    private File getScriptFile(String url) {
        String parent = context.getRealPath( basePath != null ? basePath : "/" );
        return new File( parent, url );
    }

    public String getRootUrl() {
        return basePath;
    }
}
