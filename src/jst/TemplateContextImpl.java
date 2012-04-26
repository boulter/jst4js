package jst;

import org.mozilla.javascript.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class TemplateContextImpl implements TemplateContext {
    public static final int JAVASCRIPT_VERSION = 170;
    private static final Logger logger = Logger.getLogger( TemplateContextImpl.class );

    protected Scriptable parent;
    protected final Map<String,ServerSideTemplate> compiledScripts;
    protected Script defaultScript;
    protected boolean production = false;
    protected List<TemplateLoader> loaders;
    private String sanitizingFunction;


    public TemplateContextImpl() throws IOException {
        this( null, new HashMap<String,ServerSideTemplate>() );
        initializeTopLevel();
    }

    public TemplateContextImpl(Scriptable parent, Map<String, ServerSideTemplate> compliedScripts) {
        this.parent = parent;
        this.compiledScripts = compliedScripts;
        this.loaders = new ArrayList<TemplateLoader>();
        this.loaders.add( new ResourceTemplateLoader("jst/scripts") );
    }

    protected TemplateContext initializeTopLevel() throws IOException {
        Context context;
        try {
            context = Context.enter();
            if( Context.isValidLanguageVersion(JAVASCRIPT_VERSION) ) {
                context.setLanguageVersion(JAVASCRIPT_VERSION);
            }
            parent = new ImporterTopLevel( context );
            logger.debug("Loading jst boot script...");
            Script jst = loadScript( context, "core/jst.js");
            jst.exec( context, parent );
            logger.debug("Loading template boot script...");
            defaultScript = loadScript( context, "core/template.js" );

            ScriptableObject.putProperty( parent, "logger", Context.javaToJS( logger, parent ) );
            ScriptableObject.putProperty( parent, "templateContext", Context.javaToJS( this, parent ) );
            return this;
        } finally {
            Context.exit();
        }

    }

    public TemplateContext addLoader( TemplateLoader loader ) {
        loaders.add( loader );
        return this;
    }

    public List<TemplateLoader> getLoaders() {
        return loaders;
    }

    public ScriptRuntime load(String url) throws IOException {
        ScriptRuntime runtime = new ScriptRuntime( loadTemplate(url), this );
        runtime.execute( defaultScript );
        return runtime;
    }

    public ScriptRuntime start() throws IOException {
        ScriptRuntime runtime = new ScriptRuntime( this );
        runtime.execute( defaultScript );
        return runtime;
    }

    public String getSanitizingFunction() {
        return sanitizingFunction;
    }

    public void setSanitizingFunction(String sanitizingFunction) {
        this.sanitizingFunction = sanitizingFunction;
    }

    protected ServerSideTemplate loadTemplate( String url ) throws IOException {
        synchronized( compiledScripts ) {
            if( !compiledScripts.containsKey( url ) ) {
                ServerSideTemplate template = new ServerSideTemplate( url, urlToFile(url), !production );
                if( sanitizingFunction != null ) {
                    template.setSanitizingFunction( sanitizingFunction );
                }
                compiledScripts.put( url, template );
            }
            return compiledScripts.get( url );
        }
    }

    public TemplateLoader urlToFile(String url) throws IOException {
        for( int i = loaders.size() - 1; i >= 0; i-- ) {
            TemplateLoader loader = loaders.get(i);
            InputStream stream = loader.load( url );
            if( stream != null ) {
                return loader;
            }
        }
        throw new FileNotFoundException( url );
    }

    protected Script loadScript( Context context, String url ) throws IOException {
        Reader scriptReader = new InputStreamReader( urlToFile(url).load(url) );
        try {
            return context.compileReader( scriptReader, url, 1, null );
        } finally {
            scriptReader.close();
        }
    }

    public Scriptable getParent() {
        return parent;
    }

    public void include(String url) throws IOException {
        // this is only accessible from JavascriptFilter, not from javascript.
        Context context = Context.enter();
        try {
            Script script = loadScript( context, url );
            script.exec( context, parent );
        } finally {
            Context.exit();
        }
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }

    public Object evaluate(String template, Map<String, Object> parameters) throws IOException {
        ScriptRuntime runtime = load(template);
        for( String key : parameters.keySet() ) {
            runtime.addVariable( key, parameters.get(key) );
        }
        return runtime.invoke();
    }
}
