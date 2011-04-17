package jgo.compiler
package parser

import interm._
import types._
import codeseq._
import instr._

trait ExprUtils {
  self: Base =>
  
  def badExpr(msg: String, args: AnyRef*): ExprError.type = {
    val s = String.format(msg, args: _*)
    recordErr(s)
    ExprError
  }
  
  def ifNumeric(expr: Expr)(f: (CodeBuilder, NumericType, Type) => Expr): Expr = expr match {
    case e OfType (t: NumericType) => f(expr.eval, t, expr.t)
    case _ => badExpr("operand type %s is not numeric", expr.t)
  }
  def ifIntegral(expr: Expr)(f: (CodeBuilder, IntegralType, Type) => Expr): Expr = expr match {
    case e OfType (t: IntegralType) => f(expr.eval, t, expr.t)
    case _ => badExpr("operand type %s is not integral", expr.t)
  }
  def ifUnsigned(expr: Expr)(f: (CodeBuilder, UnsignedType, Type) => Expr): Expr = expr match {
    case e OfType (t: UnsignedType) => f(expr.eval, t, expr.t)
    case _ => badExpr("operand type %s is not unsigned", expr.t)
  }
  
  
  def ifPtr(expr: Expr)(f: (CodeBuilder, Type) => Expr): Expr = expr match {
    case e OfType PointerType(elemT) => f(expr.eval, elemT)
    case _ => badExpr("operand type %s is not a pointer type", expr.t)
  }
  def ifArray(expr: Expr)(f: (CodeBuilder, Type) => Expr): Expr = expr match {
    case e OfType ArrayType(_, elemT) => f(expr.eval, elemT)
    case _ => badExpr("operand type %s is not an array type", expr.t)
  }
  def ifSlice(expr: Expr)(f: (CodeBuilder, Type) => Expr): Expr = expr match {
    case e OfType SliceType(elemT) => f(expr.eval, elemT)
    case _ => badExpr("operand type %s is not a slice type", expr.t)
  }
  def ifChan(expr: Expr)(f: (CodeBuilder, Type) => Expr): Expr = expr match {
    case e OfType ChanType(elemT, _, _) => f(expr.eval, elemT)
    case _ => badExpr("operand type %s is not a channel type", expr.t)
  }
  
  def ifSameNumeric(e1: Expr, e2: Expr)(f: (CodeBuilder, CodeBuilder, NumericType, Type) => Expr): Expr =
    if (e1.t != e2.t)
      badExpr("operands have differing types %s and %s", e1.t, e2.t)
    else
      ifNumeric(e2) { f(e1.eval, _, _, _) }
  
  def ifSameIntegral(e1: Expr, e2: Expr)(f: (CodeBuilder, CodeBuilder, IntegralType, Type) => Expr): Expr =
    if (e1.t != e2.t)
      badExpr("operands have differing types %s and %s", e1.t, e2.t)
    else
      ifIntegral(e2) { f(e1.eval, _, _, _) }
  
  def ifSameUnsigned(e1: Expr, e2: Expr)(f: (CodeBuilder, CodeBuilder, UnsignedType, Type) => Expr): Expr =
    if (e1.t != e2.t)
      badExpr("operands have differing types %s and %s", e1.t, e2.t)
    else
      ifUnsigned(e2) { f(e1.eval, _, _, _) }
  
  def ifValidShift(e1: Expr, e2: Expr)(f: (CodeBuilder, CodeBuilder, IntegralType, UnsignedType, Type) => Expr): Expr =
    ifIntegral(e1) { (code1, intT, typ1) =>
      ifUnsigned(e2) { (code2, unsT, typ2) =>
        f(code1, code2, intT, unsT, typ1)
      }
    }
  
  
  implicit def toArith(t: NumericType): Arith = t match {
    case Uint8      => U8
    case Uint16     => U16
    case Uint32     => U32
    case Uint64     => U64
    case Int8       => I8
    case Int16      => I16
    case Int32      => I32
    case Int64      => I64
    case Float32    => F32
    case Float64    => F64
    case Complex64  => C64
    case Complex128 => C128
  } 
  implicit def toArith(t: IntegralType): Integral = t match {
    case Uint8      => U8
    case Uint16     => U16
    case Uint32     => U32
    case Uint64     => U64
    case Int8       => I8
    case Int16      => I16
    case Int32      => I32
    case Int64      => I64
  } 
  implicit def toArith(t: UnsignedType): Unsigned = t match {
    case Uint8      => U8
    case Uint16     => U16
    case Uint32     => U32
    case Uint64     => U64
  }
}