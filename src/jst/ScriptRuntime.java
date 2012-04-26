package jst;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScriptRuntime {
    private static final Logger logger = Logger.getLogger( ScriptRuntime.class );

    private TemplateContextImpl context;
    private ServerSideTemplate template;
    private ServerSideTemplate layout;
    private Context jsContext;
    private Scriptable scope;
    private Map<String,String> variableMappings = new HashMap<String,String>();

    public ScriptRuntime( TemplateContextImpl context ) {
        this( null, context );
    }

    public ScriptRuntime( ServerSideTemplate template, TemplateContextImpl context ) {
        logger.info("Initialize script runtime");
        this.template = template;
        this.context = context;
        this.jsContext = Context.enter();
        this.jsContext.setWrapFactory( new EnhancedWrapFactory() );
        this.jsContext.setLanguageVersion(TemplateContextImpl.JAVASCRIPT_VERSION);

        scope = jsContext.newObject( context.getParent() );
        scope.setPrototype( context.getParent() );
        scope.setParentScope( null );

        addGlobalVariable( "runtime", this );
        addGlobalVariable( "logger", logger );
    }

    public void setLanguageVersion( int version ) {
        if( Context.isValidLanguageVersion( version ) ) {
            jsContext.setLanguageVersion( version );
        }
    }

    public int getLanguageVersion() {
        return jsContext.getLanguageVersion();
    }

    public Object invoke() throws IOException {
        long start = System.currentTimeMillis();
        try {
            logger.debug( "Invoking script " + template.getURL() );
            return template.execute( scope, jsContext, layout, variableMappings );
        } finally {
            Context.exit();
            if( logger.isDebugEnabled() ) {
                logger.debug( template.getName() + " rendered " + (System.currentTimeMillis() - start) + " ms");
            }
        }
    }

    public ScriptRuntime mixin(String mixinName, Object mixin) {
        addVariable( "Template.__" + mixinName, mixin );

        Set<String> methods = new HashSet<String>();

        Class mixinClass = mixin.getClass();

        while( mixinClass != Object.class ) {
            for(Method method : mixinClass.getMethods() ) {
                if( !methods.contains( method.getName() ) ) {
                    String script = String.format("Template.%1$s = function() { return this.__%2$s[\"%1$s\"].apply( this.__%2$s, arguments ); }", method.getName(), mixinName );
                    jsContext.evaluateString( scope, script, "Mixin " + mixinName + "." + method.getName() + "()", 1, null );
                    methods.add( method.getName() );
                }
            }
            mixinClass = mixinClass.getSuperclass();
        }
        return this;
    }

    public ScriptRuntime addGlobalVariable( String varName, Object variable ) {
        Scriptable current = scope;
        String[] varPath = varName.split("\\.");
        for( int i = 0; current != null && i < varPath.length; i++ ) {
            if( i + 1 >= varPath.length ) {
                ScriptableObject.putProperty( current, varPath[i], Context.javaToJS( variable, current ) );
                break;
            } else {
                Object value = ScriptableObject.getProperty( current, varPath[i] );
                if( value instanceof Scriptable ) {
                    current = (Scriptable)value;
                } else {
                    current = null;
                }
            }
        }
        return this;
    }

    public ScriptRuntime addVariable(String varName, Object variable) {
        String globalVariable = "__jstVariable" + (variableMappings.size() + 1);
        if( logger.isDebugEnabled() ) {
            logger.debug( "Adding variable " + globalVariable + "=" + varName );
        }
        variableMappings.put( varName, globalVariable );
        ScriptableObject.putProperty( scope, globalVariable, Context.javaToJS( variable, scope ) );
        return this;
    }

    public Object execute(Script script) {
        return script.exec( jsContext, scope );
    }

    public ServerSideTemplate read( String url ) throws IOException {
        if( !url.endsWith(".jst") ) {
            url += ".jst";
        }

        ServerSideTemplate template = context.loadTemplate( url );
        template.include( jsContext, scope );
        return template;
    }

    public void include(String url) throws IOException {
        Script script = context.loadScript( jsContext, url );
        script.exec( jsContext, scope );
    }

    public void setLayout(String url) throws IOException {
        layout = context.loadTemplate( url );
    }

    public Object evaluate( String script ) throws IOException {
        return jsContext.evaluateString( scope, script, "command line", 1, null );
    }

    public boolean stringIsCompilableUnit(String line) {
        return jsContext.stringIsCompilableUnit(line);
    }
}
