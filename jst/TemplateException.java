package jst;

public class TemplateException extends RuntimeException {
    ServerSideTemplate template;

    public TemplateException( ServerSideTemplate template, String message ) {
        super( message );
        this.template = template;
    }

    public TemplateException(ServerSideTemplate serverSideTemplate, Throwable ex) {
        super( ex.getMessage(), ex );
        this.template = serverSideTemplate;
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
