package jst.spring;

import jst.ScriptRuntime;
import jst.TemplateContext;
import jst.FileTemplateLoader;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.io.File;

public class JavascriptTemplateBean {
    private TemplateContext context;

    private Map<String,Object> variables;
    private Map<String,Object> mixins;
    private List<File> resourcePaths = new ArrayList<File>();

    public JavascriptTemplateBean() {
        variables = new HashMap<String,Object>();
        mixins = new HashMap<String,Object>();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getMixins() {
        return mixins;
    }

    public void setMixins(Map<String, Object> mixins) {
        this.mixins = mixins;
    }

    public List<File> getResourcePaths() {
        return resourcePaths;
    }

    public void setResourcePaths(List<File> resourcePaths) {
        this.resourcePaths = resourcePaths;
    }

    public ScriptRuntime load( String url ) throws IOException {
        initializeContext();

        ScriptRuntime runtime = context.load( url );

        for( String name : variables.keySet() ) {
            runtime.addVariable( name, variables.get( name ) );
        }

        for( String name : mixins.keySet() ) {
            runtime.mixin( name, mixins.get( name ) );
        }

        return runtime;
    }

    public Object evaluate( String url, Map<String,Object> data ) throws IOException {
        ScriptRuntime runtime = load(url);
        for( String key : data.keySet() ) {
            runtime.addVariable( key, data.get(key) );
        }
        return runtime.invoke();
    }

    private void initializeContext() throws IOException {
        if( context == null ) {
            context = new TemplateContext();
            for( File resourcePath : resourcePaths ) {
                context.addLoader( new FileTemplateLoader( resourcePath ) );
            }
        }
    }

}
