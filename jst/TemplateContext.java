package jst;

import org.mozilla.javascript.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class TemplateContext {
    public static final int JAVASCRIPT_VERSION = 170;
    private static final Logger logger = Logger.getLogger( TemplateContext.class );

    protected Scriptable parent;
    protected final Map<String,ServerSideTemplate> compiledScripts;
    protected Script defaultScript;
    protected boolean production = false;
    protected List<TemplateLoader> loaders;
    private String sanitizingFunction;


    public TemplateContext() throws IOException {
        this( null, new HashMap<String,ServerSideTemplate>() );
        initializeTopLevel();
    }

    public TemplateContext( Scriptable parent, Map<String,ServerSideTemplate> compliedScripts ) {
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

    public ScriptRuntime load( String url ) throws IOException {
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

    private TemplateLoader urlToFile(String url) throws IOException {
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
        Context context = Context.enter();
        try {
            Script script = loadScript( context, url );
            script.exec( context, parent );
        } finally {
            Context.exit();
        }
    }

    public Object eval(String input) {
        Context context = Context.getCurrentContext();
        if( context == null ) {
            context = Context.enter();
        }
        return context.evaluateString( getParent(), input, "cmdline", 1, null );
    }
}
