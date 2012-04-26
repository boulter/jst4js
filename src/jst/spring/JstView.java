package jst.spring;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jst.ScriptRuntime;
import jst.TemplateException;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

public class JstView extends AbstractUrlBasedView {

  private JavascriptTemplateBean templates;

  public JstView(String url, JavascriptTemplateBean templates) {
    super(url);
    this.templates = templates;
  }

  @SuppressWarnings("unchecked")
  protected void renderMergedOutputModel(@SuppressWarnings("rawtypes") Map objects, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
    try {
      ScriptRuntime runtime = templates.load(this.getUrl());
      runtime.addGlobalVariable("request", httpServletRequest);
      runtime.addGlobalVariable("response", httpServletResponse);
      runtime.addGlobalVariable("servletContext", getServletContext());

      for (String name : ((Map<String, Object>) objects).keySet()) {
        runtime.addVariable(name, objects.get(name));
      }

      Object value = runtime.invoke();

      writeResponse(httpServletResponse, value);
    } catch (TemplateException ex) {
      ScriptRuntime runtime = templates.load("templates/exception.jst");

      runtime.addGlobalVariable("request", httpServletRequest);
      runtime.addGlobalVariable("response", httpServletResponse);
      runtime.addGlobalVariable("servletContext", getServletContext());
      runtime.addVariable("ex", ex);

      writeResponse(httpServletResponse, runtime.invoke());
    }
  }

  private void writeResponse(HttpServletResponse httpServletResponse, Object value) throws IOException {
    PrintWriter writer = httpServletResponse.getWriter();
    try {
      httpServletResponse.setContentType(this.getContentType());
      writer.print(value);
    } finally {
      writer.flush();
      writer.close();
    }
  }

  @Override
  public String getContentType() {

    String result = super.getContentType();

    if (this.getUrl() == null)
      return result;
    
    MimeUtil2 mime = new MimeUtil2();

    String extension = MimeUtil2.getExtension(this.getUrl());
    
    if (extension != null && extension.equals("csv"))
      return "text/csv";
          
    MimeType type = MimeUtil2.getMostSpecificMimeType(mime.getMimeTypes(this.getUrl()));

    if (type == null || type == MimeUtil2.UNKNOWN_MIME_TYPE)
      return result;

    return type.toString();

  }
}
