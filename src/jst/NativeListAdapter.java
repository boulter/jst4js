package jst;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import java.util.List;

/**
 */
public class NativeListAdapter extends NativeJavaObject {

  private static final long serialVersionUID = -9164127346020731139L;

  public NativeListAdapter(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
    super(scope, javaObject, staticType);
    Scriptable scriptable = (Scriptable) cx.evaluateString(scope, "Array.prototype", "internal", 1, null);
    setPrototype(scriptable);
  }

  @SuppressWarnings("rawtypes")
  private List getList() {
    return (List) javaObject;
  }

  @Override
  public Object get(String name, Scriptable start) {
    if (name.equals("length"))
      return getList().size();
    return super.get(name, start);
  }

  public void delete(int index) {
    try {
      getList().remove(index);
    } catch (RuntimeException e) {
      throw Context.throwAsScriptRuntimeEx(e);
    }
  }

  public Object get(int index, Scriptable start) {
    Context cx = Context.getCurrentContext();
    try {
      int s = getList().size();
      if (index >= 0 && index < s) {
        return cx.getWrapFactory().wrap(cx, this, getList().get(index), null);
      } else {
        return Context.getUndefinedValue();
      }
    } catch (RuntimeException e) {
      throw Context.throwAsScriptRuntimeEx(e);
    }
  }

  public String getClassName() {
    return "NativeListAdapter";
  }

  public Object[] getIds() {
    int size = getList().size();
    Integer[] ids = new Integer[size];
    for (int i = 0; i < size; ++i) {
      ids[i] = i;
    }
    return ids;
  }

  public boolean has(int index, Scriptable start) {
    return index >= 0 && index < getList().size();
  }

  @SuppressWarnings("unchecked")
  public void put(int index, Scriptable start, Object value) {
    try {
      getList().set(index, Context.jsToJava(value, org.mozilla.javascript.ScriptRuntime.ObjectClass));
    } catch (RuntimeException e) {
      Context.throwAsScriptRuntimeEx(e);
    }
  }

  @Override
  public boolean hasInstance(Scriptable value) {
    return true;
  }

  public String toString() {
    return javaObject.toString();
  }
}
