
public class InvokeStatic {
  static String string = new String();

  static String x;

  static {
    x = "abc";
  }

  public static void repro(int a, String b, boolean c) {
  }

  public static void repro1(Object x) {
    Object y = x;
    y.toString();
  }

  public static void repro2(Object x) {
    Object y = "";
    y.toString();
  }
}
