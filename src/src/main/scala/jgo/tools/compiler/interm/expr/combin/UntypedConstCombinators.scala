package jgo.tools.compiler
package interm
package expr
package combin

import types._
import instr._
import instr.TypeConversions._
import codeseq._

trait UntypedConstCombinators extends Combinators {
  private implicit def wrapInResult[T](v: T): Err[T] = result(v)
  
  private def boolConst(b: Boolean) =
    TypedBoolConst(b, scope.UniverseScope.bool)
  
  
  abstract override def eval(e: Expr): CodeBuilder = e match {
    case c: UntypedConst => CodeBuilder.empty //A statement like "5;" should generate no code.
    case _ => super.eval(e)
  }
  
  
  protected abstract override def convertForAssign(e: Expr, t: Type, desc: String)(pos: Pos) = e match {
    case c: UntypedConst => c.withType(t) match {
      case Some(typedConst) => result(typedConst)
      case None => problem("%s %s is incompatible with target type %s", desc, c.valueString, t)(pos)
    }
    case _ => super.convertForAssign(e, t, desc)(pos)
  }
  
  
  abstract override def and(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedBoolConst(b1), UntypedBoolConst(b2)) => UntypedBoolConst(b1 && b2)
    case _ => super.and(e1, e2)(pos)
  }
  
  abstract override def or(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedBoolConst(b1), UntypedBoolConst(b2)) => UntypedBoolConst(b1 || b2)
    case _ => super.or(e1, e2)(pos)
  }
  
  abstract override def not(e: Expr)(pos: Pos) = e match {
    case UntypedBoolConst(b) => UntypedBoolConst(!b)
    case _ => super.not(e)(pos)
  }
  
  
  abstract override def compEq(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedStringConst(v1), UntypedStringConst(v2)) => boolConst(v1 == v2)
    case (UntypedBoolConst(v1),   UntypedBoolConst(v2))   => boolConst(v1 == v2)
    
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => boolConst(i1 == i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => boolConst(r1 == r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) => boolConst(r1 == r2 && i1 == i2)
    
    case _ => super.compEq(e1, e2)(pos)
  }
  
  abstract override def compNe(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedStringConst(v1), UntypedStringConst(v2)) => boolConst(v1 != v2)
    case (UntypedBoolConst(v1),   UntypedBoolConst(v2))   => boolConst(v1 != v2)
    
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => boolConst(i1 != i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => boolConst(r1 != r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) => boolConst(r1 != r2 || i1 != i2)
    
    case _ => super.compNe(e1, e2)(pos)
  }
  
  abstract override def compLt(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => boolConst(i1 < i2)
    case (UntypedRealConst(r1),     UntypedRealConst(r2))     => boolConst(r1 < r2)
    case _ => super.compLt(e1, e2)(pos)
  }
  
  abstract override def compLeq(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => boolConst(i1 <= i2)
    case (UntypedRealConst(r1),     UntypedRealConst(r2))     => boolConst(r1 <= r2)
    case _ => super.compLeq(e1, e2)(pos)
  }
  
  abstract override def compGt(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => boolConst(i1 > i2)
    case (UntypedRealConst(r1),     UntypedRealConst(r2))     => boolConst(r1 > r2)
    case _ => super.compGt(e1, e2)(pos)
  }
  
  abstract override def compGeq(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => boolConst(i1 >= i2)
    case (UntypedRealConst(r1),     UntypedRealConst(r2))     => boolConst(r1 >= r2)
    case _ => super.compGeq(e1, e2)(pos)
  }
  
  
  abstract override def plus(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedStringConst(str1), UntypedStringConst(str2)) => UntypedStringConst(str1 + str2)
    
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => UntypedIntegralConst(i1 + i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => UntypedFloatingConst(r1 + r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) => UntypedComplexConst(r1 + r2, i1 + i2)
    
    case _ => super.plus(e1, e2)(pos)
  }
  
  abstract override def minus(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => UntypedIntegralConst(i1 - i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => UntypedFloatingConst(r1 - r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) => UntypedComplexConst(r1 - r2, i1 - i2)
    case _ => super.minus(e1, e2)(pos)
  }
  
  abstract override def times(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => UntypedIntegralConst(i1 * i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => UntypedFloatingConst(r1 * r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) => UntypedComplexConst(r1*r2 - i1*i2,
                                                                                           r1*i2 + r2*i1)
    case _ => super.times(e1, e2)(pos)
  }
  
  abstract override def div(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1),    UntypedIntegralConst(i2))    => UntypedIntegralConst(i1 / i2)
    case (UntypedRealConst(r1),        UntypedRealConst(r2))        => UntypedFloatingConst(r1 / r2)
    case (UntypedNumericConst(r1, i1), UntypedNumericConst(r2, i2)) =>
      val normSq2 = r2*r2 + i2*i2
      val r = (r1*r2 + i1*i2) / normSq2
      var i = (i2*r1 - r2*i1) / normSq2
      UntypedComplexConst(r, i)
    case _ => super.div(e1, e2)(pos)
  }
  
  abstract override def mod(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => UntypedIntegralConst(i1 % i2)
    case _ => super.mod(e1, e2)(pos)
  }
  
  abstract override def positive(e: Expr)(pos: Pos) = e match {
    case i: UntypedIntegralConst => i
    case f: UntypedFloatingConst => f
    case c: UntypedComplexConst  => c
    case _ => super.positive(e)(pos)
  }
  
  abstract override def negative(e: Expr)(pos: Pos) = e match {
    case UntypedIntegralConst(i)   => UntypedIntegralConst(-i)
    case UntypedFloatingConst(r)   => UntypedFloatingConst(-r)
    case UntypedComplexConst(r, i) => UntypedComplexConst(-r, -i)
    case _ => super.negative(e)(pos)
  }
  
  
  abstract override def bitAnd(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => UntypedIntegralConst(i1 & i2)
    case _ => super.bitAnd(e1, e2)(pos)
  }
  
  abstract override def bitAndNot(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => UntypedIntegralConst(i1 &~ i2)
    case _ => super.bitAndNot(e1, e2)(pos)
  }
  
  abstract override def bitOr(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => UntypedIntegralConst(i1 | i2)
    case _ => super.bitOr(e1, e2)(pos)
  }
  
  abstract override def bitXor(e1: Expr, e2: Expr)(pos: Pos) = (e1, e2) match {
    case (UntypedIntegralConst(i1), UntypedIntegralConst(i2)) => UntypedIntegralConst(i1 ^ i2)
    case _ => super.bitXor(e1, e2)(pos)
  }
  
  //def shiftL(e1: Expr, e2: Expr) (implicit pos: Pos): Err[Expr]
  //def shiftR(e1: Expr, e2: Expr) (implicit pos: Pos): Err[Expr]
  
  abstract override def bitCompl(e: Expr)(pos: Pos) = e match {
    case UntypedIntegralConst(i) => UntypedIntegralConst(i~) //BigInt defines ~ instead of unary_~. Filed a bug report.
    case _ => super.bitCompl(e)(pos)                         //https://issues.scala-lang.org/browse/SI-4659
  }
}
