package jst;

import java.io.IOException;
import java.util.Map;

public interface TemplateContext {

    ScriptRuntime load(String url) throws IOException;

    void include(String url) throws IOException;

    Object evaluate(String template, Map<String, Object> parameters) throws IOException;

}
