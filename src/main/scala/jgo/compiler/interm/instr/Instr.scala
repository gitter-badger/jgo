package jgo.compiler
package interm
package instr

import symbols._
import types._
import member._

sealed abstract class Instr

case class Decl(v: LocalVar) extends Instr
case class UnDecl(v: LocalVar) extends Instr

case object Deref extends Instr
case object Enref extends Instr
case object Copy  extends Instr

case class New(typeOf: Type) extends Instr

case class InvokeFunc(func: FuncSymbol) extends Instr
case object InvokeLambda
case object Return extends Instr

case class Add(t: Arith) extends Instr
case class Sub(t: Arith) extends Instr
case class Mul(t: Arith) extends Instr
case class Div(t: Arith) extends Instr

case class Mod(t: Integral) extends Instr

case class ShiftL(t1: Integral, t2: Unsigned) extends Instr
case class ShiftR(t1: Integral, t2: Unsigned) extends Instr

case class Goto(target: Label) extends Instr
case class Branch(b: BooleanTree, target: Label) extends Instr
case class Lbl(l: Label) extends Instr

case object Pop       extends Instr
case object Dup       extends Instr
case object Dup_Down1 extends Instr
case object Dup_Down2 extends Instr
case object Swap      extends Instr

case class LoadLocal(variable: LocalVar) extends Instr
case class StoreLocal(variable: LocalVar) extends Instr

case class LoadGlobal(v: GlobalVar) extends Instr
case class StoreGlobal(v: GlobalVar) extends Instr

case class GetField(t: Type, f: Field) extends Instr
case class PutField(t: Type, f: Field) extends Instr

case class ArrayGet(elemT: Type) extends Instr
case class SliceGet(elemT: Type) extends Instr
case class MapGet(keyT: Type, valueT: Type) extends Instr

case class ArrayPut(elemT: Type) extends Instr
case class SlicePut(elemT: Type) extends Instr
case class MapPut(keyT: Type, valueT: Type) extends Instr

case class ChanSend(elemT: Type) extends Instr
case class ChanRecv(elemT: Type) extends Instr
