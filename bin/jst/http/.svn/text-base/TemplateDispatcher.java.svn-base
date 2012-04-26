package jst.http;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

public class TemplateDispatcher {
    public static final String JST_MIXIN = "jst.mixin";
    public static final String JST_VARIABLE = "jst.variable.";
    public static final String JST_LAYOUT = "jst.layout.";
    public static final String JST_SCRIPT_MIXIN = "jst.script.";
    public static final String JST_SCRIPT = "jst.template";

    private static final Logger logger = Logger.getLogger( TemplateDispatcher.class );

    private HttpServletRequest request;
    private HttpServletResponse response;

    public TemplateDispatcher(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public TemplateDispatcher exposeVariable( String varname, Object obj ) {
        request.setAttribute( JST_VARIABLE + varname, obj );
        return this;
    }

    public TemplateDispatcher mixin( String varname, Object mixin ) {
        request.setAttribute( JST_MIXIN + varname, mixin );
        return this;
    }

    public TemplateDispatcher mixin( String scriptFile ) {
        request.setAttribute( JST_SCRIPT_MIXIN + scriptFile, scriptFile );
        return this;
    }

    public TemplateDispatcher layout( String layout ) {
        request.setAttribute( JST_LAYOUT, layout );
        return this;
    }

    public TemplateDispatcher script( String name ) {
        request.setAttribute( JST_SCRIPT_MIXIN + name, name );
        return this;
    }

    public void forward( String template ) throws IOException, ServletException {
        request.setAttribute( JST_SCRIPT, template );
        request.getRequestDispatcher( template ).forward( request, response );
    }

    public void include( String template ) throws IOException, ServletException {
        request.getRequestDispatcher( template ).include( request, response );
    }

}
