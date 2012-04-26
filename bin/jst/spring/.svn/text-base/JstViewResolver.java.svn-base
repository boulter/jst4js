package jst.spring;

import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class JstViewResolver extends UrlBasedViewResolver {

    private JavascriptTemplateBean templates;

    public JstViewResolver() {
        setViewClass( requiredViewClass() ); 
    }

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
}
