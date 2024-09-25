package challenge

/**
 * @param workers rows of workers parallel to conveyor
 */
case class State(finishedProductsInBucket: Int,
                 conveyor: Seq[ConveyorSlot],
                 workers: Array[Array[Worker]]) {
  
  override def toString: String = {
    s"Finished products in bucket: $finishedProductsInBucket\n" +
      workers(0).mkString("", " ", "\n") +
      conveyor.map { 
        case AvailableSlot() => "   "
        case UnavailableSlot() => " - "
        case Product() => Item.P.toString.padTo(3, ' ')
        case Component(item) => item.toString.padTo(3, ' ')
      }.mkString("", " ", "\n") +
      workers(1).mkString(" ")
  }

  def finishedProductsOnConveyor: Int = conveyor.count(cs => cs == Product())
  def finishedProductsInHands: Int = workers.flatten.flatMap(w => w.items).count(i => i == Item.P)
  def totalFinishedProducts: Int = finishedProductsInBucket + finishedProductsOnConveyor + finishedProductsInHands
}