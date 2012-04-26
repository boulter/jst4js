package jst;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

import java.util.List;
import java.util.Map;

/**
 * This class wraps Java java.util.Maps and java.util.Lists such that they
 * behave just like Javascript's associative arrays and arrays respectively.
 * This allows you to treat java.util.Maps and java.util.Lists as either native
 * Javascript objects, or use Java's methods.
 */
public class EnhancedWrapFactory extends WrapFactory {

  private boolean scriptableJavaObjects = false;

  public EnhancedWrapFactory() {
    setJavaPrimitiveWrap(false);
  }

  public EnhancedWrapFactory(boolean scriptableJavaObjects) {
    this();
    this.scriptableJavaObjects = scriptableJavaObjects;
  }

  public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
    if (javaObject instanceof Map) {
      return new NativeMapAdapter(cx, scope, javaObject, staticType);
    } else if (javaObject instanceof List) {
      return new NativeListAdapter(cx, scope, javaObject, staticType);
    } else if (scriptableJavaObjects) {
      return new ScriptableNativeJavaObject(scope, javaObject, staticType);
    } else {
      return new NativeJavaObject(scope, javaObject, staticType);
    }
  }
}
