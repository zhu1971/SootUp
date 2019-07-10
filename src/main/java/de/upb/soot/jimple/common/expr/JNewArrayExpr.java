/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

package de.upb.soot.jimple.common.expr;

import de.upb.soot.DefaultIdentifierFactory;
import de.upb.soot.jimple.Jimple;
import de.upb.soot.jimple.basic.JimpleComparator;
import de.upb.soot.jimple.basic.Value;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.visitor.ExprVisitor;
import de.upb.soot.jimple.visitor.Visitor;
import de.upb.soot.types.ArrayType;
import de.upb.soot.types.Type;
import de.upb.soot.util.Copyable;
import de.upb.soot.util.printer.StmtPrinter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class JNewArrayExpr implements Expr, Copyable {

  private final Type baseType;
  private final ValueBox sizeBox;

  public JNewArrayExpr(Type type, Value size) {
    this.baseType = type;
    this.sizeBox = Jimple.newImmediateBox(size);
  }

  @Override
  public boolean equivTo(Object o, JimpleComparator comparator) {
    return comparator.caseNewArrayExpr(this, o);
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return sizeBox.getValue().equivHashCode() * 101 + baseType.hashCode() * 17;
  }

  @Override
  public String toString() {
    return (Jimple.NEWARRAY + " (")
        + getBaseTypeString()
        + ")"
        + "["
        + sizeBox.getValue().toString()
        + "]";
  }

  /** Converts a parameter of type StmtPrinter to a string literal. */
  @Override
  public void toString(StmtPrinter up) {
    up.literal(Jimple.NEWARRAY);
    up.literal(" ");
    up.literal("(");
    up.typeSignature(baseType);
    up.literal(")");
    up.literal("[");
    sizeBox.toString(up);
    up.literal("]");
  }

  private String getBaseTypeString() {
    return baseType.toString();
  }

  public Type getBaseType() {
    return baseType;
  }

  public ValueBox getSizeBox() {
    return sizeBox;
  }

  public Value getSize() {
    return sizeBox.getValue();
  }

  /** Returns a list of type ValueBox, contains a list of values of sizeBox. */
  @Override
  public final List<ValueBox> getUseBoxes() {

    List<ValueBox> useBoxes = new ArrayList<>(sizeBox.getValue().getUseBoxes());
    useBoxes.add(sizeBox);

    return useBoxes;
  }

  /** Returns an instance of ArrayType(). */
  @Override
  public Type getType() {
    if (baseType instanceof ArrayType) {
      return DefaultIdentifierFactory.getInstance()
          .getArrayType(
              ((ArrayType) baseType).getBaseType(), ((ArrayType) baseType).getDimension() + 1);
    } else {
      return DefaultIdentifierFactory.getInstance().getArrayType(baseType, 1);
    }
  }

  @Override
  public void accept(Visitor sw) {
    ((ExprVisitor) sw).caseNewArrayExpr(this);
  }

  @Nonnull
  public JNewArrayExpr withBaseType(Type baseType) {
    return new JNewArrayExpr(baseType, getSize());
  }

  @Nonnull
  public JNewArrayExpr withSize(Value size) {
    return new JNewArrayExpr(baseType, size);
  }
}