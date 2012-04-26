package jst;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import java.util.Map;

/**
 */

public class NativeMapAdapter extends NativeJavaObject {
    public NativeMapAdapter(Context cx, Scriptable scope, Object javaObject, Class staticType) {
        super(scope, javaObject, staticType);
        Scriptable scriptable = (Scriptable) cx.evaluateString( scope, "Object.prototype", "internal" , 1, null);
        setPrototype(scriptable);
    }

    private Map getMap() {
        return (Map) javaObject;
    }

    public void delete(String name) {
        try {
            getMap().remove(name);
        } catch (RuntimeException e) {
            Context.throwAsScriptRuntimeEx(e);
        }
    }

    public Object get(String name, Scriptable start) {
        Object value = super.get(name, start);
        if (value != Scriptable.NOT_FOUND) {
            return value;
        }

        value = getMap().get(name);

        if (value == null) {
            return Scriptable.NOT_FOUND;
        }

        Context cx = Context.getCurrentContext();
        return cx.getWrapFactory().wrap(cx, this, value, null);
    }

    public String getClassName() {
        return "NativeMapAdapter";
    }

    public Object[] getIds() {
        return getMap().keySet().toArray();
    }

    public boolean has(String name, Scriptable start) {
        return getMap().containsKey(name) || super.has(name, start);
    }

    public void put(String name, Scriptable start, Object value) {
        try {
            getMap().put(name, Context.jsToJava(value, org.mozilla.javascript.ScriptRuntime.ObjectClass));
        } catch (RuntimeException e) {
            Context.throwAsScriptRuntimeEx(e);
        }
    }

    public String toString() {
        return javaObject.toString();
    }
}
