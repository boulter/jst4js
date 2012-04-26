package jst;

import java.io.*;

public class FileTemplateLoader implements TemplateLoader {

    File home;

    public FileTemplateLoader(File home) {
        this.home = home;
    }

    public InputStream load(String url) throws IOException {
        File scriptFile = new File( home, url );
        if( scriptFile.exists() ) {
            return new BufferedInputStream( new FileInputStream( scriptFile ) );
        } else {
            return null;
        }
    }

    public boolean shouldRefresh(String url, long compiledTimestamp ) {
        File scriptFile = new File( home, url );
        return compiledTimestamp < scriptFile.lastModified();
    }

    public String getRootUrl() {
        return home.getAbsolutePath();
    }
}
