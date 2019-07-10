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

import de.upb.soot.jimple.Jimple;
import de.upb.soot.jimple.basic.JimpleComparator;
import de.upb.soot.jimple.basic.Value;
import de.upb.soot.jimple.basic.ValueBox;
import de.upb.soot.jimple.visitor.ExprVisitor;
import de.upb.soot.jimple.visitor.Visitor;
import de.upb.soot.types.Type;
import de.upb.soot.util.Copyable;
import de.upb.soot.util.printer.StmtPrinter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

public final class JCastExpr implements Expr, Copyable {

  private final ValueBox opBox;
  private final Type type;

  public JCastExpr(Value op, Type type) {
    this.opBox = Jimple.newImmediateBox(op);
    this.type = type;
  }

  @Override
  public boolean equivTo(Object o, JimpleComparator comparator) {
    return comparator.caseCastExpr(this, o);
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return opBox.getValue().equivHashCode() * 101 + type.hashCode() + 17;
  }

  @Override
  public String toString() {
    return "(" + type.toString() + ") " + opBox.getValue().toString();
  }

  @Override
  public void toString(StmtPrinter up) {
    up.literal("(");
    up.typeSignature(type);
    up.literal(") ");
    opBox.toString(up);
  }

  public Value getOp() {
    return opBox.getValue();
  }

  public ValueBox getOpBox() {
    return opBox;
  }

  @Override
  public final List<ValueBox> getUseBoxes() {

    List<ValueBox> list = new ArrayList<>(opBox.getValue().getUseBoxes());
    list.add(opBox);

    return list;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public void accept(Visitor sw) {
    ((ExprVisitor) sw).caseCastExpr(this);
  }

  @Nonnull
  public JCastExpr withOp(Value op) {
    return new JCastExpr(op, type);
  }

  @Nonnull
  public JCastExpr withType(Type type) {
    return new JCastExpr(getOp(), type);
  }
}