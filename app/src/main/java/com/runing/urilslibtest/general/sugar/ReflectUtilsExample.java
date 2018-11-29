package com.runing.urilslibtest.general.sugar;

import com.runing.utilslib.general.sugar.ReflectUtils;

public class ReflectUtilsExample {

  private static final class Target {
    private String str = "123";
    public static int sInt = 7;

    private int getNumber(Target target) {
      return target.hashCode();
    }

    public static String getStr() {
      return "testStr";
    }
  }

  public void test() {
    Target target = new Target();

//    /* 1-1. 操作对象的成员变量 */
    try {
      // 注入。
      ReflectUtils.inject(target)
          .field("str")
          .set("rts");
    } catch (Exception e) {
      /* 如果需要判断异常类型 */
      if (e instanceof NoSuchFieldException) { }
      else if (e instanceof IllegalAccessException) { }
      else { throw new AssertionError("UNKNOWN ERROR."); }
    }

    try {
      // 读取。
      String str = (String) ReflectUtils.inject(target)
          .field("str")
          .get();
    } catch (Exception ignore) {}

    /* 1-2. 操作类的静态成员变量 */
    try {
      ReflectUtils.inject(Target.class)
          .field("sInt")
          .set(2);
    } catch (Exception ignore) {}

    try {
      int sInt = (int) ReflectUtils.inject(Target.class)
          .field("sInt")
          .get();
    } catch (Exception ignore) {}

    /* 2-1. 操作对象的方法 */
    try {
      int hashCode = (int) ReflectUtils.invoke(target)
          .method("getNumber")
          .paramsType(Target.class)
          .invoke(target);
    } catch (Exception ignore) {}


    /* 2-2. 操作类的静态方法 */
    try {
      String str = (String) ReflectUtils.invoke(Target.class)
          .method("getStr")
          .invoke();
    } catch (Exception ignore) {}
  }
}
