package jst;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

public class ResourceTemplateLoader implements TemplateLoader {

  private String basePath;

  public ResourceTemplateLoader() {
  }

  public ResourceTemplateLoader(String basePath) {
    this.basePath = basePath;
  }

  private String buildResourcePath(String url) {
    return "/" + (basePath != null ? basePath + "/" + url : url);
  }

  public InputStream load(String url) throws IOException {

    File f = new File(url);

    String filename = f.getName();

    File parent = f;

    // walk up the tree, looking for matches
    
    do {

      parent = parent.getParentFile();

      String path = (parent == null ? "" : parent.getPath() + File.separator ) + filename;

      InputStream stream = getClass().getResourceAsStream(buildResourcePath(path));

      if (stream != null)
        return stream;

      stream = getClass().getResourceAsStream(buildResourcePath(path) + ".jst");

      if (stream != null)
        return stream;

    }  while (parent != null);


    return null;
  }

  public boolean shouldRefresh(String url, long compiledTimestamp) {
    return false;
  }

  public String getRootUrl() {
    return basePath;
  }
}
