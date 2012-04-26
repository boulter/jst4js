package jst.test;

import jst.http.TemplateDispatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestJavaScriptFilterServlet extends HttpServlet {


    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException {
        String test = httpServletRequest.getParameter("test");
        if( "layout".equals(test) ) {
            new TemplateDispatcher( httpServletRequest, httpServletResponse )
                    .exposeVariable( "today", new Date() )
                    .exposeVariable( "count", 69 )
                    .mixin( "servlet", this )
                    .layout( "layout.jst" )
                    .forward("timeOfDay.jst");
        } else if( "invitation".equals(test) ) {
            new TemplateDispatcher( httpServletRequest, httpServletResponse )
                    .forward("invitation.jst");
        } else if( "include".equals(test) ) {
            new TemplateDispatcher( httpServletRequest, httpServletResponse )
                    .forward("include.jst");
        } else {
            List views = Arrays.asList( "layout", "invitation", "include" );
            new TemplateDispatcher( httpServletRequest, httpServletResponse )
                    .exposeVariable( "views", views )
                    .forward("index.jst");
        }
    }

    public String css( String css ) {
        return "<link href=\"" + css + "\" rel=\"stylesheet\" type=\"text/css\"/>";
    }
}
