package challenge

enum Item:
  case A, B, P
  
object Item:
  val components: Seq[Item] = Seq(A, B)