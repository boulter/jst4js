package jst.spring;

import jst.ScriptRuntime;
import jst.TemplateException;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.io.PrintWriter;

public class JstView extends AbstractUrlBasedView {

    private JavascriptTemplateBean templates;

    public JstView(String url, JavascriptTemplateBean templates) {
        super(url);
        this.templates = templates;
    }

    protected void renderMergedOutputModel(Map objects, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        try {
            ScriptRuntime runtime = templates.load( this.getUrl() );
            runtime.addGlobalVariable( "request", httpServletRequest );
            runtime.addGlobalVariable( "response", httpServletResponse );
            runtime.addGlobalVariable( "servletContext", getServletContext() );

            for( String name : ((Map<String,Object>)objects).keySet() ) {
                runtime.addVariable( name, objects.get( name ) );
            }

            Object value = runtime.invoke();

            writeResponse(httpServletResponse, value);
        } catch( TemplateException ex ) {
            ScriptRuntime runtime = templates.load( "templates/exception.jst" );

            runtime.addGlobalVariable( "request", httpServletRequest);
            runtime.addGlobalVariable( "response", httpServletResponse );
            runtime.addGlobalVariable( "servletContext", getServletContext() );
            runtime.addVariable("ex", ex );

            writeResponse( httpServletResponse, runtime.invoke() );
        }
    }

    private void writeResponse(HttpServletResponse httpServletResponse, Object value) throws IOException {
        PrintWriter writer = httpServletResponse.getWriter();
        try {
            httpServletResponse.setContentType( this.getContentType() );
            writer.print( value );
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
