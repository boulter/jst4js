package jst.spring;

import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.io.PrintWriter;

public class JstView extends AbstractUrlBasedView {

    private JavascriptTemplateBean templates;

    public JstView(String url, JavascriptTemplateBean templates) {
        super(url);
        this.templates = templates;
    }

    protected void renderMergedOutputModel(Map objects, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        Object value = templates.evaluate( this.getUrl(), objects );
        PrintWriter writer = httpServletResponse.getWriter();
        httpServletResponse.setContentType( this.getContentType() );
        writer.print( value );
        writer.close();
    }
}
