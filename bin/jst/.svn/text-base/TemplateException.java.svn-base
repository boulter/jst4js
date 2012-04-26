package jst;

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.RhinoException;

import java.util.ArrayList;
import java.util.List;

public class TemplateException extends RuntimeException {
    private ServerSideTemplate template;
    private List<StackTraceElement> scriptStackTrace;
    private String source;
    private int lineNumber = -1;

    public TemplateException(ServerSideTemplate serverSideTemplate, RhinoException ex) {
        super( ex.getMessage(), ex );
        this.template = serverSideTemplate;
        this.lineNumber = ex.lineNumber();
        this.source = ex instanceof EcmaError ? ((EcmaError)ex).getSourceName() : null;

        scriptStackTrace = new ArrayList<StackTraceElement>();
        for( StackTraceElement element : ex.getStackTrace() ) {
            if( element.getLineNumber() >= 0 && (element.getFileName().endsWith("jst") || element.getFileName().endsWith("js")) ) {
                scriptStackTrace.add( element );
            }
        }
    }

    public ServerSideTemplate getTemplate() {
        return template;
    }

    public int getScriptLineNumber() {
        return lineNumber;
    }

    public int getTemplateLineNumber() {
        return lineNumber > 0 ? template.getTemplateLineFromScriptLine( lineNumber ) : -1;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer( super.toString() );
        buffer.append( "\nTemplate source for ");
        buffer.append( template.getName() );
        buffer.append( ":\n" );
        buffer.append( template );
        return buffer.toString();
    }
}
