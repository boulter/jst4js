package jst;

import org.mozilla.javascript.*;

public class ScriptableNativeJavaObject extends ScriptableObject {

    NativeJavaObject prototype;

    public ScriptableNativeJavaObject(Scriptable scope, Object javaObject, Class staticType) {
        super(scope, new NativeJavaObject(scope, javaObject, staticType));
        this.prototype = (NativeJavaObject) this.getPrototype();
    }

    public String getClassName() {
        return prototype.unwrap().getClass().getName();
    }

    public static class ScriptableNativeContextFactory extends ContextFactory {
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setWrapFactory(new ScriptableNativeWrapFactory());
            return cx;
        }
    }

    public static class ScriptableNativeWrapFactory extends WrapFactory {
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType) {
            return new ScriptableNativeJavaObject(scope, javaObject, staticType);
        }
    }


    public static void main(String[] args) {
        new ScriptableNativeContextFactory().call(new ContextAction() {
            public Object run(Context cx) {
                return cx.evaluateString(cx.initStandardObjects(),
                        "var o = new java.lang.Object();" +
                        "o.name = 'bar'; " +
                        "java.lang.System.out.println(o.name + ': ' + o.hashCode())",
                        "str", 1, null);
            }
        });
    }
}
