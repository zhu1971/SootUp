package de.upb.sse.sootup.test.java.sourcecode.minimaltestsuite.java6;

import static org.junit.Assert.assertEquals;

import categories.Java8Test;
import de.upb.sse.sootup.core.jimple.basic.Local;
import de.upb.sse.sootup.core.jimple.basic.Value;
import de.upb.sse.sootup.core.jimple.common.ref.JInstanceFieldRef;
import de.upb.sse.sootup.core.jimple.common.stmt.JAssignStmt;
import de.upb.sse.sootup.core.jimple.common.stmt.Stmt;
import de.upb.sse.sootup.core.model.Body;
import de.upb.sse.sootup.core.model.SootClass;
import de.upb.sse.sootup.core.model.SootMethod;
import de.upb.sse.sootup.core.signatures.MethodSignature;
import de.upb.sse.sootup.core.types.ClassType;
import de.upb.sse.sootup.core.types.Type;
import de.upb.sse.sootup.java.core.types.JavaClassType;
import de.upb.sse.sootup.test.java.sourcecode.minimaltestsuite.MinimalSourceTestSuiteBase;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Java8Test.class)
public class NestedClassShadowTest extends MinimalSourceTestSuiteBase {

  JavaClassType nestedClass =
      identifierFactory.getClassType(
          getClassName(customTestWatcher.getClassPath()) + "$NestedClass");
  SootClass<?> sootNestedClass = loadClass(nestedClass);

  /** Test: OuterClass of NestedClass is NestedClassShadow */
  @Test
  public void testOuterClass() {
    assertEquals(getDeclaredClassSignature(), sootNestedClass.getOuterClass().get());
  }

  /** Test: How many Locals with ClassType {@link java.lang.String} */
  @Test
  public void testNumOfLocalsWithString() {
    SootMethod method = sootNestedClass.getMethod(getMethodSignature().getSubSignature()).get();
    Body methodBody = method.getBody();
    Set<Local> locals = methodBody.getLocals();
    Set<Local> stringLocals =
        locals.stream()
            .filter(local -> local.getType().toString().equals("java.lang.String"))
            .collect(Collectors.toSet());
    assertEquals(3, stringLocals.size());
  }

  /** Test: Locals--info are from different classes */
  @Test
  public void testClassesOfStringLocalAreDifferent() {
    SootMethod method = sootNestedClass.getMethod(getMethodSignature().getSubSignature()).get();
    Body methodBody = method.getBody();
    List<Stmt> stmts = methodBody.getStmts();
    Set<Type> classTypes = new HashSet<Type>();
    for (Stmt stmt : stmts) {
      if (stmt instanceof JAssignStmt) {
        final Value rightOp = ((JAssignStmt) stmt).getRightOp();
        if (rightOp instanceof JInstanceFieldRef) {
          final ClassType declClassType =
              ((JInstanceFieldRef) rightOp).getFieldSignature().getDeclClassType();
          classTypes.add(declClassType);
        }
      }
    }
    assertEquals(2, classTypes.size());
  }

  @Override
  public MethodSignature getMethodSignature() {
    return identifierFactory.getMethodSignature(
        nestedClass, "printInfo", "void", Collections.singletonList("java.lang.String"));
  }
}