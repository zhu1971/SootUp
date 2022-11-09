package de.upb.sse.sootup.core.jimple.common.expr;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999-2020 Patrick Lam, Christian Brüggemann, Linghui Luo
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import de.upb.sse.sootup.core.jimple.basic.Immediate;
import de.upb.sse.sootup.core.jimple.visitor.ExprVisitor;
import de.upb.sse.sootup.core.util.Copyable;
import javax.annotation.Nonnull;

/** An expression that computes a binary OR of two operands. */
public final class JOrExpr extends AbstractIntLongBinopExpr implements Copyable {

  public JOrExpr(@Nonnull Immediate op1, @Nonnull Immediate op2) {
    super(op1, op2);
  }

  @Nonnull
  @Override
  public String getSymbol() {
    return " | ";
  }

  @Override
  public void accept(@Nonnull ExprVisitor v) {
    v.caseOrExpr(this);
  }

  @Nonnull
  public JOrExpr withOp1(@Nonnull Immediate op1) {
    return new JOrExpr(op1, getOp2());
  }

  @Nonnull
  public JOrExpr withOp2(@Nonnull Immediate op2) {
    return new JOrExpr(getOp1(), op2);
  }
}