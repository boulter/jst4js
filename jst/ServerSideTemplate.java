package jst;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.mozilla.javascript.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.MessageFormat;

public class ServerSideTemplate {
    private static final Pattern jsDelimeters = Pattern.compile("<[%@]={0,2}(.+?)-?[@%]>", Pattern.MULTILINE );
    private static final Logger logger = Logger.getLogger( ServerSideTemplate.class );

    private String name;
    private String url;
    private String generatedSource;
    private TemplateLoader sourceLoader;
    private Script script;
    private long compiledTimestamp;
    private boolean isDebug = true;
    private String sanitizingFunction = null;
    private List<String> formalParameters = new ArrayList<String>();

    public ServerSideTemplate(String url, TemplateLoader scriptLoader, boolean isDebug ) throws FileNotFoundException {
        this.url = url;
        this.sourceLoader = scriptLoader;
        this.name = toFunctionName();
        this.isDebug = isDebug;
    }

    private String createPageScript( CharSequence template ) {
        StringBuilder currentBuffer = new StringBuilder();
        Matcher matcher = jsDelimeters.matcher( template );
        int start = 0;
        while( start != template.length() ) {
            if( matcher.find( start ) ) {
                int plainTextEnd = matcher.start();

                currentBuffer.append( createOutput( template.subSequence( start, plainTextEnd ) ) );
                String expression = matcher.group();
                String scriptletContent = matcher.group(1);

                if( expression.startsWith("<%==") ) {
                    currentBuffer.append( createInlineExpression( scriptletContent, false ) );
                } else if( expression.startsWith("<%=") ) {
                    currentBuffer.append( createInlineExpression( scriptletContent, true ) );
                } else if( expression.startsWith("<@") ) {
                    String[] vars = scriptletContent.split(",");
                    for( String variable : vars ) {
                        formalParameters.add( variable.trim() );
                    }
                } else {
                    addExpresion(currentBuffer, scriptletContent);
                }
                start = matcher.end();
                if( expression.endsWith("-%>") ) {
                    start = eatRemainingWhitespace(start, template, currentBuffer);
                }

            } else if( start < template.length() ) {
                currentBuffer.append( createOutput( template.subSequence( start, template.length() ) ) );
                start = template.length();
            } else {
                start = template.length();
            }
        }
        wrapFunction(currentBuffer);

        this.compiledTimestamp = System.currentTimeMillis();
        return currentBuffer.toString();
    }

    private int eatRemainingWhitespace(int start, CharSequence template, StringBuilder currentBuffer) {
        for( int i = start; i < template.length(); i++ ) {
            if( template.charAt(i) == '\n' ) {
                currentBuffer.append( createOutput( template.subSequence( start, i ).toString().trim() ) );
                start = i + 1;
                break;
            } else if( !Character.isWhitespace( template.charAt(i) ) ) {
                break;
            }
        }
        return start;
    }

    private void wrapFunction(StringBuilder currentBuffer) {
        currentBuffer.insert( 0, MessageFormat.format( "function {0}( {1} ) '{'\n", name, StringUtil.join(formalParameters, "," ) ) );
        currentBuffer.append( "return this.__output.join('');\n");
        currentBuffer.append( "}\n" );
    }

    private String toFunctionName() {
        return "_" + StringUtil.toCamelCase( stripExtension( url ) ).replaceAll( escapeSeparator(), "_" );
    }

    private String escapeSeparator() {
        return File.separator.equals("\\") ? "\\\\" : File.separator;
    }

    private String stripExtension(String scriptFile) {
        int extension = scriptFile.lastIndexOf('.');
        int start = scriptFile.indexOf("/") + 1;
        if( extension > 0 ) {
            return scriptFile.substring(start, extension);
        } else {
            return scriptFile.substring(start);
        }
    }

    private void addExpresion(StringBuilder currentBuffer, String scriptletContent) {
        if( scriptletContent.length() > 0 ) {
            currentBuffer.append( scriptletContent );
            currentBuffer.append( "\n" );
        }
    }

    private CharSequence createInlineExpression(String inlineExpression, boolean sanitize) {
        if( inlineExpression.length() > 0 ) {
            inlineExpression = stripTrailingSemiColon(inlineExpression);
            if( sanitize && sanitizingFunction != null ) {
                return "this.__output.push( " + sanitizingFunction + "( " + inlineExpression + ") );\n";
            } else {
                return "this.__output.push( " + inlineExpression + ");\n";
            }
        } else {
            return "";
        }
    }

    private String stripTrailingSemiColon(String inlineExpression) {
        inlineExpression = inlineExpression.trim();
        if( inlineExpression.endsWith(";") ) {
            inlineExpression = inlineExpression.substring(0, inlineExpression.length() - 1 );
        }
        return inlineExpression;
    }

    private CharSequence createOutput(CharSequence charSequence) {
        if( charSequence.length() > 0 ) {
            return "this.__output.push('" + StringEscapeUtils.escapeJavaScript( charSequence.toString() ) + "' );\n";
        } else {
            return "";
        }
    }

    private StringBuffer readTemplate(InputStream scriptStream) throws IOException {
        StringBuffer temp = new StringBuffer();
        try {
            LineNumberReader reader = new LineNumberReader( new InputStreamReader( scriptStream ) );
            String line;
            while( (line = reader.readLine() ) != null ) {
                temp.append(line);
                temp.append("\n");
            }
        } finally {
            scriptStream.close();
        }
        return temp;
    }

    public boolean shouldRefresh() {
        return script == null || ( isDebug && sourceLoader.shouldRefresh( url, compiledTimestamp ) );
    }

    public synchronized Script compile( Context cx ) throws IOException {
        try {
            if( logger.isInfoEnabled() ) {
                logger.info("Compiling view for url: " + url );
            }
            script = cx.compileString( getGeneratedSource(), url, 1, null );
            return script;
        } catch( EvaluatorException ex ) {
            throw new TemplateException( this, ex );
        } finally {
            if( logger.isDebugEnabled() ) {
                logger.debug( getGeneratedSource() );
            }
        }
    }

    public String toString() {
        try {
            return getGeneratedSource();
        } catch( IOException e ) {
            throw (RuntimeException)new RuntimeException( e.getMessage() ).initCause( e );
        }
    }

    private String createTemplate() throws IOException {
        formalParameters.clear();
        InputStream stream = sourceLoader.load( url );
        if( stream == null ) throw new IOException( url + " not found!" );
        StringBuffer template = readTemplate( stream );
        return createPageScript( template);
    }

    public Script getScript() {
        return script;
    }

    public String getName() {
        return name;
    }

    public String getGeneratedSource() throws IOException {
        if( generatedSource == null || shouldRefresh() ) {
            generatedSource = createTemplate();
        }
        return generatedSource;
    }

    // todo consider moving this to the runtime since this is a generated method to kick off the intial template only.
    public Object execute(Scriptable scope, Context context, ServerSideTemplate layout, Map<String,String> callByName) throws IOException {
        List<String> actualParameters = new ArrayList<String>( callByName.size() );
        for( String formalParam : formalParameters) {
            actualParameters.add( callByName.get( formalParam ) );
        }

        include(context, scope);

        String javaObj = "__java__";
        ScriptableObject.putProperty( scope, javaObj, Context.javaToJS( this, scope ) );

        StringBuilder startScript = new StringBuilder();
        startScript.append("var __jst__ = function() {\n");
        startScript.append(MessageFormat.format("  var template = new Template({0},{1});\n", getName(), javaObj) );
        if( layout != null ) {
            layout.include(context, scope);
            startScript.append(MessageFormat.format("  template.__layout = {0};\n", layout.getName() ) );
        }
        startScript.append(MessageFormat.format( "  return template.evaluate({0});\n", StringUtil.join(actualParameters, ",") ) );
        startScript.append("};\n");
        startScript.append("__jst__()");
        return context.evaluateString( scope, startScript.toString(), "jst_evaluate", 1, null );
    }

    public void include(Context context, Scriptable scope) throws IOException {
        synchronized( this ) {
            if( shouldRefresh() ) {
                compile( context );
            }
        }
        getScript().exec( context, scope );
    }

    public String getSanitizingFunction() {
        return sanitizingFunction;
    }

    public void setSanitizingFunction(String sanitizingFunction) {
        this.sanitizingFunction = sanitizingFunction;
    }

    public List<String> getFormalParameters() {
        return formalParameters;
    }

    public static void main( String[] args ) throws IOException {
        FileTemplateLoader loader = new FileTemplateLoader( new File( "web/resources/test" ) );
        ServerSideTemplate script = new ServerSideTemplate( "timeOfDay.jst", loader, true);
        System.out.println( script.toString() );
//        File layoutFile = new File( "web/resources/default.jst" );
//        ServerSideTemplate layout = new ServerSideTemplate( "asset/list", layoutFile, true);
//        System.out.println( layout.toString() );
    }

}
