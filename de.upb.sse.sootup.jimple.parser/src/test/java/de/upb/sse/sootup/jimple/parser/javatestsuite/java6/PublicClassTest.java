package de.upb.sse.sootup.jimple.parser.javatestsuite.java6;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.upb.sse.sootup.core.model.Modifier;
import de.upb.sse.sootup.core.model.SootClass;
import de.upb.sse.sootup.core.model.SootMethod;
import de.upb.sse.sootup.core.signatures.MethodSignature;
import de.upb.sse.sootup.jimple.parser.categories.Java8Test;
import de.upb.sse.sootup.jimple.parser.javatestsuite.JimpleTestSuiteBase;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/** @author Kaustubh Kelkar */
@Category(Java8Test.class)
public class PublicClassTest extends JimpleTestSuiteBase {

  @Test
  public void test() {
    SootClass<?> clazz = loadClass(getDeclaredClassSignature());
    assertEquals(EnumSet.of(Modifier.PUBLIC, Modifier.SYNCHRONIZED), clazz.getModifiers());

    SootMethod method;
    method = clazz.getMethod(getMethodSignature("private").getSubSignature()).get();
    assertTrue(method.isPrivate());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("protected").getSubSignature()).get();
    assertTrue(method.isProtected());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("public").getSubSignature()).get();
    assertTrue(method.isPublic());
    assertJimpleStmts(method, expectedBodyStmts());

    method = clazz.getMethod(getMethodSignature("noModifier").getSubSignature()).get();
    assertTrue(method.getModifiers().isEmpty());
    assertJimpleStmts(method, expectedBodyStmts());
  }

  public MethodSignature getMethodSignature(String modifier) {
    return identifierFactory.getMethodSignature(
        getDeclaredClassSignature(), modifier + "Method", "void", Collections.emptyList());
  }

  public List<String> expectedBodyStmts() {
    return Stream.of("l0 := @this: PublicClass", "return").collect(Collectors.toList());
  }
}