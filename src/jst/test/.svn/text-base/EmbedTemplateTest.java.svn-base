package jst.test;

import jst.ScriptRuntime;
import jst.FileTemplateLoader;
import jst.TemplateContextImpl;

import java.io.IOException;
import java.io.File;

public class EmbedTemplateTest {

    public static void main(String[] args) throws IOException {
        TemplateContextImpl context = new TemplateContextImpl();
        context.addLoader( new FileTemplateLoader( new File("./test") ) );
        ScriptRuntime runtime = context.load("/embedded-test.jst");
        Object output = runtime.addVariable("name", "Charlie").addVariable("age", 31).invoke();

        System.out.println( output );
    }
}
