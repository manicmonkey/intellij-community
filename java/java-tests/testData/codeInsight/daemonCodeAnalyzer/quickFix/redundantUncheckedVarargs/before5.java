// "Remove redundant "unchecked" suppression" "true"
import java.util.*;

public class Test {
  @SafeVarargs
  static <T> List<T> foo(T... t){
    return null;
  }

  @SuppressWarnings("unc<caret>hecked")
  void foo() {
    List<ArrayList<String>> list = foo(new ArrayList<String>());
  }
}

