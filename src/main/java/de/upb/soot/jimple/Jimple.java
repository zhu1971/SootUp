/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
/*
 * Modified by the Sable Research Group and others 1997-1999.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package de.upb.soot.jimple;

import de.upb.soot.jimple.basic.ConditionExprBox;
import de.upb.soot.jimple.basic.IdentityRefBox;
import de.upb.soot.jimple.basic.ImmediateBox;
import de.upb.soot.jimple.basic.InvokeExprBox;
import de.upb.soot.jimple.basic.JStmtBox;
import de.upb.soot.jimple.basic.JTrap;
import de.upb.soot.jimple.basic.Local;
import de.upb.soot.jimple.basic.LocalBox;
import de.upb.soot.jimple.basic.PositionInfo;
import de.upb.soot.jimple.basic.StmtBox;
import de.upb.soot.jimple.basic.Value;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.common.constant.IntConstant;
import de.upb.soot.jimple.common.expr.JAddExpr;
import de.upb.soot.jimple.common.expr.JAndExpr;
import de.upb.soot.jimple.common.expr.JCastExpr;
import de.upb.soot.jimple.common.expr.JCmpExpr;
import de.upb.soot.jimple.common.expr.JCmpgExpr;
import de.upb.soot.jimple.common.expr.JCmplExpr;
import de.upb.soot.jimple.common.expr.JDivExpr;
import de.upb.soot.jimple.common.expr.JDynamicInvokeExpr;
import de.upb.soot.jimple.common.expr.JEqExpr;
import de.upb.soot.jimple.common.expr.JGeExpr;
import de.upb.soot.jimple.common.expr.JGtExpr;
import de.upb.soot.jimple.common.expr.JInstanceOfExpr;
import de.upb.soot.jimple.common.expr.JInterfaceInvokeExpr;
import de.upb.soot.jimple.common.expr.JLeExpr;
import de.upb.soot.jimple.common.expr.JLengthExpr;
import de.upb.soot.jimple.common.expr.JLtExpr;
import de.upb.soot.jimple.common.expr.JMulExpr;
import de.upb.soot.jimple.common.expr.JNeExpr;
import de.upb.soot.jimple.common.expr.JNegExpr;
import de.upb.soot.jimple.common.expr.JNewArrayExpr;
import de.upb.soot.jimple.common.expr.JNewExpr;
import de.upb.soot.jimple.common.expr.JNewMultiArrayExpr;
import de.upb.soot.jimple.common.expr.JOrExpr;
import de.upb.soot.jimple.common.expr.JRemExpr;
import de.upb.soot.jimple.common.expr.JShlExpr;
import de.upb.soot.jimple.common.expr.JShrExpr;
import de.upb.soot.jimple.common.expr.JSpecialInvokeExpr;
import de.upb.soot.jimple.common.expr.JStaticInvokeExpr;
import de.upb.soot.jimple.common.expr.JSubExpr;
import de.upb.soot.jimple.common.expr.JUshrExpr;
import de.upb.soot.jimple.common.expr.JVirtualInvokeExpr;
import de.upb.soot.jimple.common.expr.JXorExpr;
import de.upb.soot.jimple.common.ref.JArrayRef;
import de.upb.soot.jimple.common.ref.JCaughtExceptionRef;
import de.upb.soot.jimple.common.ref.JInstanceFieldRef;
import de.upb.soot.jimple.common.ref.JParameterRef;
import de.upb.soot.jimple.common.ref.JStaticFieldRef;
import de.upb.soot.jimple.common.ref.JThisRef;
import de.upb.soot.jimple.common.stmt.JAssignStmt;
import de.upb.soot.jimple.common.stmt.JGotoStmt;
import de.upb.soot.jimple.common.stmt.JIdentityStmt;
import de.upb.soot.jimple.common.stmt.JIfStmt;
import de.upb.soot.jimple.common.stmt.JInvokeStmt;
import de.upb.soot.jimple.common.stmt.JNopStmt;
import de.upb.soot.jimple.common.stmt.JReturnStmt;
import de.upb.soot.jimple.common.stmt.JReturnVoidStmt;
import de.upb.soot.jimple.common.stmt.JThrowStmt;
import de.upb.soot.jimple.common.stmt.Stmt;
import de.upb.soot.jimple.javabytecode.stmt.JBreakpointStmt;
import de.upb.soot.jimple.javabytecode.stmt.JEnterMonitorStmt;
import de.upb.soot.jimple.javabytecode.stmt.JExitMonitorStmt;
import de.upb.soot.jimple.javabytecode.stmt.JLookupSwitchStmt;
import de.upb.soot.jimple.javabytecode.stmt.JRetStmt;
import de.upb.soot.jimple.javabytecode.stmt.JTableSwitchStmt;
import de.upb.soot.signatures.FieldSignature;
import de.upb.soot.signatures.MethodSignature;
import de.upb.soot.types.ArrayType;
import de.upb.soot.types.JavaClassType;
import de.upb.soot.types.NullType;
import de.upb.soot.types.PrimitiveType;
import de.upb.soot.types.ReferenceType;
import de.upb.soot.types.Type;
import de.upb.soot.types.VoidType;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The Jimple class contains all the constructors for the components of the Jimple grammar for the
 * Jimple body. <br>
 * <br>
 *
 * <p>Immediate -> Local | Constant <br>
 * RValue -> Local | Constant | ConcreteRef | Expr<br>
 * Variable -> Local | ArrayRef | InstanceFieldRef | StaticFieldRef <br>
 */
public class Jimple {
  public static final String NEWARRAY = "newarray";
  public static final String NEWMULTIARRAY = "newmultiarray";
  public static final String NOP = "nop";
  public static final String RET = "ret";
  public static final String SPECIALINVOKE = "specialinvoke";
  public static final String DYNAMICINVOKE = "dynamicinvoke";
  public static final String STATICINVOKE = "staticinvoke";
  public static final String TABLESWITCH = "tableswitch";
  public static final String VIRTUALINVOKE = "virtualinvoke";
  public static final String NULL_TYPE = "null_type";
  public static final String UNKNOWN = "unknown";
  public static final String CMP = "cmp";
  public static final String CMPG = "cmpg";
  public static final String CMPL = "cmpl";
  public static final String ENTERMONITOR = "entermonitor";
  public static final String EXITMONITOR = "exitmonitor";
  public static final String INTERFACEINVOKE = "interfaceinvoke";
  public static final String LENGTHOF = "lengthof";
  public static final String LOOKUPSWITCH = "lookupswitch";
  public static final String NEG = "neg";
  public static final String IF = "if";
  public static final String ABSTRACT = "abstract";
  public static final String BOOLEAN = "boolean";
  public static final String BREAK = "break";
  public static final String BYTE = "byte";
  public static final String CASE = "case";
  public static final String CATCH = "catch";
  public static final String CHAR = "char";
  public static final String CLASS = "class";
  public static final String FINAL = "final";
  public static final String NATIVE = "native";
  public static final String PUBLIC = "public static";
  public static final String PROTECTED = "protected";
  public static final String PRIVATE = "private";
  public static final String STATIC = "static";
  public static final String SYNCHRONIZED = "synchronized";
  public static final String TRANSIENT = "transient";
  public static final String VOLATILE = "volatile";
  public static final String STRICTFP = "strictfp";
  public static final String ENUM = "enum";
  public static final String ANNOTATION = "annotation";
  public static final String INTERFACE = "interface";
  public static final String VOID = "void";
  public static final String SHORT = "short";
  public static final String INT = "int";
  public static final String LONG = "long";
  public static final String FLOAT = "float";
  public static final String DOUBLE = "double";
  public static final String EXTENDS = "extends";
  public static final String IMPLEMENTS = "implements";
  public static final String BREAKPOINT = "breakpoint";
  public static final String DEFAULT = "default";
  public static final String GOTO = "goto";
  public static final String INSTANCEOF = "instanceof";
  public static final String NEW = "new";
  public static final String RETURN = "return";
  public static final String THROW = "throw";
  public static final String THROWS = "throws";
  public static final String NULL = "null";
  public static final String FROM = "from";
  public static final String TO = "to";
  public static final String WITH = "with";
  public static final String CLS = "cls";
  public static final String TRUE = "true";
  public static final String FALSE = "false";

  /** Returns a list of collections. */
  public static List<String> jimpleKeywordList() {
    List<String> l = new LinkedList<>();
    Collections.addAll(
        l,
        NEWARRAY,
        NEWMULTIARRAY,
        NOP,
        RET,
        SPECIALINVOKE,
        STATICINVOKE,
        TABLESWITCH,
        VIRTUALINVOKE,
        NULL_TYPE,
        UNKNOWN,
        CMP,
        CMPG,
        CMPL,
        ENTERMONITOR,
        EXITMONITOR,
        INTERFACEINVOKE,
        LENGTHOF,
        LOOKUPSWITCH,
        NEG,
        IF,
        ABSTRACT,
        BOOLEAN,
        BREAK,
        BYTE,
        CASE,
        CATCH,
        CHAR,
        CLASS,
        FINAL,
        NATIVE,
        PUBLIC,
        PROTECTED,
        PRIVATE,
        STATIC,
        SYNCHRONIZED,
        TRANSIENT,
        VOLATILE,
        STRICTFP,
        ENUM,
        ANNOTATION,
        INTERFACE,
        VOID,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        EXTENDS,
        IMPLEMENTS,
        BREAKPOINT,
        DEFAULT,
        GOTO,
        INSTANCEOF,
        NEW,
        RETURN,
        THROW,
        THROWS,
        NULL,
        FROM,
        TO,
        WITH,
        CLS,
        TRUE,
        FALSE);
    return l;
  }

  public static boolean isJavaKeywordType(Type t) {
    // TODO: Ensure that the check is complete.
    return t instanceof PrimitiveType || t instanceof VoidType || t instanceof NullType;
  }

  public static Value cloneIfNecessary(Value val) {
    // TODO: [JMP] Clone, if necessary
    return val;
  }

  /** Constructs a XorExpr(Immediate, Immediate) grammar chunk. */
  public static JXorExpr newXorExpr(Value op1, Value op2) {
    return new JXorExpr(op1, op2);
  }

  /** Constructs a UshrExpr(Immediate, Immediate) grammar chunk. */
  public static JUshrExpr newUshrExpr(Value op1, Value op2) {
    return new JUshrExpr(op1, op2);
  }

  /** Constructs a SubExpr(Immediate, Immediate) grammar chunk. */
  public static JSubExpr newSubExpr(Value op1, Value op2) {
    return new JSubExpr(op1, op2);
  }

  /** Constructs a ShrExpr(Immediate, Immediate) grammar chunk. */
  public static JShrExpr newShrExpr(Value op1, Value op2) {
    return new JShrExpr(op1, op2);
  }

  /** Constructs a ShlExpr(Immediate, Immediate) grammar chunk. */
  public static JShlExpr newShlExpr(Value op1, Value op2) {
    return new JShlExpr(op1, op2);
  }

  /** Constructs a RemExpr(Immediate, Immediate) grammar chunk. */
  public static JRemExpr newRemExpr(Value op1, Value op2) {
    return new JRemExpr(op1, op2);
  }

  /** Constructs a OrExpr(Immediate, Immediate) grammar chunk. */
  public static JOrExpr newOrExpr(Value op1, Value op2) {
    return new JOrExpr(op1, op2);
  }

  /** Constructs a NeExpr(Immediate, Immediate) grammar chunk. */
  public static JNeExpr newNeExpr(Value op1, Value op2) {
    return new JNeExpr(op1, op2);
  }

  /** Constructs a MulExpr(Immediate, Immediate) grammar chunk. */
  public static JMulExpr newMulExpr(Value op1, Value op2) {
    return new JMulExpr(op1, op2);
  }

  /** Constructs a LeExpr(Immediate, Immediate) grammar chunk. */
  public static JLeExpr newLeExpr(Value op1, Value op2) {
    return new JLeExpr(op1, op2);
  }

  /** Constructs a GeExpr(Immediate, Immediate) grammar chunk. */
  public static JGeExpr newGeExpr(Value op1, Value op2) {
    return new JGeExpr(op1, op2);
  }

  /** Constructs a EqExpr(Immediate, Immediate) grammar chunk. */
  public static JEqExpr newEqExpr(Value op1, Value op2) {
    return new JEqExpr(op1, op2);
  }

  /** Constructs a DivExpr(Immediate, Immediate) grammar chunk. */
  public static JDivExpr newDivExpr(Value op1, Value op2) {
    return new JDivExpr(op1, op2);
  }

  /** Constructs a CmplExpr(Immediate, Immediate) grammar chunk. */
  public static JCmplExpr newCmplExpr(Value op1, Value op2) {
    return new JCmplExpr(op1, op2);
  }

  /** Constructs a CmpgExpr(Immediate, Immediate) grammar chunk. */
  public static JCmpgExpr newCmpgExpr(Value op1, Value op2) {
    return new JCmpgExpr(op1, op2);
  }

  /** Constructs a CmpExpr(Immediate, Immediate) grammar chunk. */
  public static JCmpExpr newCmpExpr(Value op1, Value op2) {
    return new JCmpExpr(op1, op2);
  }

  /** Constructs a GtExpr(Immediate, Immediate) grammar chunk. */
  public static JGtExpr newGtExpr(Value op1, Value op2) {
    return new JGtExpr(op1, op2);
  }

  /** Constructs a LtExpr(Immediate, Immediate) grammar chunk. */
  public static JLtExpr newLtExpr(Value op1, Value op2) {
    return new JLtExpr(op1, op2);
  }

  /** Constructs a AddExpr(Immediate, Immediate) grammar chunk. */
  public static JAddExpr newAddExpr(Value op1, Value op2) {
    return new JAddExpr(op1, op2);
  }

  /** Constructs a AndExpr(Immediate, Immediate) grammar chunk. */
  public static JAndExpr newAndExpr(Value op1, Value op2) {
    return new JAndExpr(op1, op2);
  }

  /** Constructs a NegExpr(Immediate, Immediate) grammar chunk. */
  public static JNegExpr newNegExpr(Value op) {
    return new JNegExpr(op);
  }

  /** Constructs a LengthExpr(Immediate) grammar chunk. */
  public static JLengthExpr newLengthExpr(Value op) {
    return new JLengthExpr(op);
  }

  /** Constructs a CastExpr(Immediate, Type) grammar chunk. */
  public static JCastExpr newCastExpr(Value op1, Type t) {
    return new JCastExpr(op1, t);
  }

  /** Constructs a InstanceOfExpr(Immediate, Type) grammar chunk. */
  public static JInstanceOfExpr newInstanceOfExpr(Value op1, Type t) {
    return new JInstanceOfExpr(op1, t);
  }

  /** Constructs a NewArrayExpr(Type, Immediate) grammar chunk. */
  public static JNewArrayExpr newNewArrayExpr(Type type, Value size) {
    return new JNewArrayExpr(type, size);
  }

  /** Constructs a NewStaticInvokeExpr(ArrayType, List of Immediate) grammar chunk. */
  public static JStaticInvokeExpr newStaticInvokeExpr(
      MethodSignature method, List<? extends Value> args) {
    return new JStaticInvokeExpr(method, args);
  }

  public static JStaticInvokeExpr newStaticInvokeExpr(MethodSignature method, Value... args) {
    return newStaticInvokeExpr(method, Arrays.asList(args));
  }

  public static JStaticInvokeExpr newStaticInvokeExpr(MethodSignature method, Value arg) {
    return newStaticInvokeExpr(method, Collections.singletonList(arg));
  }

  public static JStaticInvokeExpr newStaticInvokeExpr(MethodSignature method) {
    return newStaticInvokeExpr(method, Collections.emptyList());
  }

  /**
   * Constructs a NewSpecialInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JSpecialInvokeExpr newSpecialInvokeExpr(
      Local base, MethodSignature method, List<? extends Value> args) {
    return new JSpecialInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewSpecialInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JSpecialInvokeExpr newSpecialInvokeExpr(
      Local base, MethodSignature method, Value... args) {
    return newSpecialInvokeExpr(base, method, Arrays.asList(args));
  }

  public static JSpecialInvokeExpr newSpecialInvokeExpr(
      Local base, MethodSignature method, Value arg) {
    return newSpecialInvokeExpr(base, method, Collections.singletonList(arg));
  }

  public static JSpecialInvokeExpr newSpecialInvokeExpr(Local base, MethodSignature method) {
    return newSpecialInvokeExpr(base, method, Collections.emptyList());
  }

  /**
   * Constructs a NewDynamicInvokeExpr(SootMethod bootstrapMethodRef, List bootstrapArgs, SootMethod
   * methodRef, List args) grammar chunk.
   */
  public static JDynamicInvokeExpr newDynamicInvokeExpr(
      MethodSignature bootstrapMethodRef,
      List<? extends Value> bootstrapArgs,
      MethodSignature methodRef,
      List<? extends Value> args) {
    return new JDynamicInvokeExpr(bootstrapMethodRef, bootstrapArgs, methodRef, args);
  }

  /**
   * Constructs a NewDynamicInvokeExpr(SootMethod bootstrapMethodRef, List bootstrapArgs, SootMethod
   * methodRef, List args) grammar chunk.
   */
  public static JDynamicInvokeExpr newDynamicInvokeExpr(
      MethodSignature bootstrapMethodRef,
      List<? extends Value> bootstrapArgs,
      MethodSignature methodRef,
      int tag,
      List<? extends Value> args) {
    return new JDynamicInvokeExpr(bootstrapMethodRef, bootstrapArgs, methodRef, tag, args);
  }

  /**
   * Constructs a NewVirtualInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JVirtualInvokeExpr newVirtualInvokeExpr(
      Local base, MethodSignature method, List<? extends Value> args) {
    return new JVirtualInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewVirtualInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JVirtualInvokeExpr newVirtualInvokeExpr(
      Local base, MethodSignature method, Value... args) {
    return newVirtualInvokeExpr(base, method, Arrays.asList(args));
  }

  public static JVirtualInvokeExpr newVirtualInvokeExpr(
      Local base, MethodSignature method, Value arg) {
    return newVirtualInvokeExpr(base, method, Collections.singletonList(arg));
  }

  public static JVirtualInvokeExpr newVirtualInvokeExpr(Local base, MethodSignature method) {
    return newVirtualInvokeExpr(base, method, Collections.emptyList());
  }

  /**
   * Constructs a NewInterfaceInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JInterfaceInvokeExpr newInterfaceInvokeExpr(
      Local base, MethodSignature method, List<? extends Value> args) {
    return new JInterfaceInvokeExpr(base, method, args);
  }

  /**
   * Constructs a NewInterfaceInvokeExpr(Local base, SootMethod method, List of Immediate) grammar
   * chunk.
   */
  public static JInterfaceInvokeExpr newInterfaceInvokeExpr(
      Local base, MethodSignature method, Value... args) {
    return newInterfaceInvokeExpr(base, method, Arrays.asList(args));
  }

  public static JInterfaceInvokeExpr newInterfaceInvokeExpr(
      Local base, MethodSignature method, Value arg) {
    return newInterfaceInvokeExpr(base, method, Collections.singletonList(arg));
  }

  public static JInterfaceInvokeExpr newInterfaceInvokeExpr(Local base, MethodSignature method) {
    return newInterfaceInvokeExpr(base, method, Collections.emptyList());
  }

  /** Constructs a ThrowStmt(Immediate) grammar chunk. */
  public static JThrowStmt newThrowStmt(Value op, PositionInfo posInfo) {
    return new JThrowStmt(op, posInfo);
  }

  /** Constructs a ExitMonitorStmt(Immediate) grammar chunk. */
  public static JExitMonitorStmt newExitMonitorStmt(Value op, PositionInfo posInfo) {
    return new JExitMonitorStmt(op, posInfo);
  }

  /** Constructs a EnterMonitorStmt(Immediate) grammar chunk. */
  public static JEnterMonitorStmt newEnterMonitorStmt(Value op, PositionInfo posInfo) {
    return new JEnterMonitorStmt(op, posInfo);
  }

  /** Constructs a BreakpointStmt() grammar chunk. */
  public static JBreakpointStmt newBreakpointStmt(PositionInfo posInfo) {
    return new JBreakpointStmt(posInfo);
  }

  /** Constructs a GotoStmt(Stmt) grammar chunk. */
  public static JGotoStmt newGotoStmt(Stmt target, PositionInfo posInfo) {
    return new JGotoStmt(target, posInfo);
  }

  public static JGotoStmt newGotoStmt(StmtBox stmtBox, PositionInfo posInfo) {
    return new JGotoStmt(stmtBox, posInfo);
  }

  /** Constructs a NopStmt() grammar chunk. */
  public static JNopStmt newNopStmt(PositionInfo posInfo) {
    return new JNopStmt(posInfo);
  }

  /** Constructs a ReturnVoidStmt() grammar chunk. */
  public static JReturnVoidStmt newReturnVoidStmt(PositionInfo posInfo) {
    return new JReturnVoidStmt(posInfo);
  }

  /** Constructs a ReturnStmt(Immediate) grammar chunk. */
  public static JReturnStmt newReturnStmt(Value op, PositionInfo posInfo) {
    return new JReturnStmt(op, posInfo);
  }

  /** Constructs a RetStmt(Local) grammar chunk. */
  public static JRetStmt newRetStmt(Value stmtAddress, PositionInfo posInfo) {
    return new JRetStmt(stmtAddress, posInfo);
  }

  /** Constructs a IfStmt(Condition, Stmt) grammar chunk. */
  public static JIfStmt newIfStmt(Value condition, Stmt target, PositionInfo posInfo) {
    return new JIfStmt(condition, target, posInfo);
  }

  /** Constructs a IfStmt(Condition, UnitBox) grammar chunk. */
  public static JIfStmt newIfStmt(Value condition, StmtBox target, PositionInfo posInfo) {
    return new JIfStmt(condition, target, posInfo);
  }

  /** Constructs a IdentityStmt(Local, IdentityRef) grammar chunk. */
  public static JIdentityStmt newIdentityStmt(
      Value local, Value identityRef, PositionInfo posInfo) {
    return new JIdentityStmt(local, identityRef, posInfo);
  }

  /** Constructs a AssignStmt(Variable, RValue) grammar chunk. */
  public static JAssignStmt newAssignStmt(Value variable, Value rvalue, PositionInfo posInfo) {
    return new JAssignStmt(variable, rvalue, posInfo);
  }

  /** Constructs a InvokeStmt(InvokeExpr) grammar chunk. */
  public static JInvokeStmt newInvokeStmt(Value op, PositionInfo posInfo) {
    return new JInvokeStmt(op, posInfo);
  }

  /** Constructs a TableSwitchStmt(Immediate, int, int, List of Unit, Stmt) grammar chunk. */
  public static JTableSwitchStmt newTableSwitchStmt(
      Value key,
      int lowIndex,
      int highIndex,
      List<? extends Stmt> targets,
      Stmt defaultTarget,
      PositionInfo posInfo) {
    return new JTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget, posInfo);
  }

  public static JTableSwitchStmt newTableSwitchStmt(
      Value key,
      int lowIndex,
      int highIndex,
      List<? extends StmtBox> targets,
      StmtBox defaultTarget,
      PositionInfo posInfo) {
    return new JTableSwitchStmt(key, lowIndex, highIndex, targets, defaultTarget, posInfo);
  }

  /**
   * Constructs a LookupSwitchStmt(Immediate, List of Immediate, List of Unit, Stmt) grammar chunk.
   */
  public static JLookupSwitchStmt newLookupSwitchStmt(
      Value key,
      List<IntConstant> lookupValues,
      List<? extends Stmt> targets,
      Stmt defaultTarget,
      PositionInfo posInfo) {
    return new JLookupSwitchStmt(key, lookupValues, targets, defaultTarget, posInfo);
  }

  public static JLookupSwitchStmt newLookupSwitchStmt(
      Value key,
      List<IntConstant> lookupValues,
      List<? extends StmtBox> targets,
      StmtBox defaultTarget,
      PositionInfo posInfo) {
    return new JLookupSwitchStmt(key, lookupValues, targets, defaultTarget, posInfo);
  }

  /** Constructs a Local with the given name and type. */
  public static Local newLocal(String name, Type t) {
    return new Local(name, t);
  }

  /** Constructs a JStaticFieldRef(FieldSignature) grammar chunk. */
  public static JStaticFieldRef newStaticFieldRef(FieldSignature f) {
    return new JStaticFieldRef(f);
  }

  /** Constructs a ThisRef(RefType) grammar chunk. */
  public static JThisRef newThisRef(ReferenceType t) {
    return new JThisRef(t);
  }

  /** Constructs a ParameterRef(SootMethod, int) grammar chunk. */
  public static JParameterRef newParameterRef(Type paramType, int number) {
    return new JParameterRef(paramType, number);
  }

  /** Constructs a InstanceFieldRef(Local, FieldSignature) grammar chunk. */
  public static JInstanceFieldRef newInstanceFieldRef(Value base, FieldSignature f) {
    return new JInstanceFieldRef(base, f);
  }

  /** Constructs a ArrayRef(Local, Immediate) grammar chunk. */
  public static JArrayRef newArrayRef(Value base, Value index) {
    return new JArrayRef(base, index);
  }

  /** Constructs a CaughtExceptionRef() grammar chunk. */
  public static JCaughtExceptionRef newCaughtExceptionRef() {
    return new JCaughtExceptionRef();
  }

  public static ValueBox newArgBox(Value value) {
    return new ImmediateBox(value);
  }

  public static ValueBox newImmediateBox(Value value) {
    return new ImmediateBox(value);
  }

  public static StmtBox newStmtBox(Stmt stmt) {
    return new JStmtBox(stmt);
  }

  public static ValueBox newLocalBox(Value local) {
    return new LocalBox(local);
  }

  public static ValueBox newIdentityRefBox(Value value) {
    return new IdentityRefBox(value);
  }

  public static ValueBox newConditionExprBox(Value condition) {
    return new ConditionExprBox(condition);
  }

  public static ValueBox newInvokeExprBox(Value value) {
    return new InvokeExprBox(value);
  }

  /** Constructs a NewExpr(RefType) grammar chunk. */
  public static JNewExpr newNewExpr(ReferenceType type) {
    return new JNewExpr(type);
  }

  public static JNewMultiArrayExpr newNewMultiArrayExpr(
      ArrayType type, List<? extends Value> sizes) {
    return new JNewMultiArrayExpr(type, sizes);
  }

  public static JTrap newTrap(
      JavaClassType exception, StmtBox beginStmt, StmtBox endStmt, StmtBox handlerStmt) {
    return new JTrap(exception, beginStmt, endStmt, handlerStmt);
  }
}