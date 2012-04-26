package jst.spring;

import java.util.Locale;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

public class JstViewResolver extends UrlBasedViewResolver {

  private JavascriptTemplateBean templates;

  public JstViewResolver() {
    setViewClass(requiredViewClass());
  }

  @SuppressWarnings("rawtypes")
  protected Class requiredViewClass() {
    return JstView.class;
  }

  protected AbstractUrlBasedView buildView(String path) throws Exception {
    return new JstView(path, templates);
  }

  public JavascriptTemplateBean getTemplates() {
    return templates;
  }

  public void setTemplates(JavascriptTemplateBean templates) {
    this.templates = templates;
  }

  @Override
  protected boolean canHandle(String viewName, Locale locale) {
    return templates.hasTemplate(viewName);
  }
}
