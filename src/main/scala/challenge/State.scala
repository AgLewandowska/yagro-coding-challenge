package challenge

import scala.annotation.tailrec

case class State(finishedProductsInBucket: Int,
                 conveyor: Seq[ConveyorSlot],
                 workers: Array[Array[Worker]]) {
  
  override def toString: String = {
    s"Finished products in bucket: $finishedProductsInBucket\n" +
      workers(0).mkString("", " ", "\n") +
      conveyor.map { 
        case AvailableSlot() | UnavailableSlot() => "   "
        case Product() => Product().item.get.toString.padTo(3, ' ')
        case Component(item) => item.toString.padTo(3, ' ')
      }.mkString("", " ", "\n") +
      workers(1).mkString(" ")
  }

  def finishedProductsOnConveyor: Int = conveyor.count(cs => cs == Product())
  def finishedProductsInHands: Int = workers.flatten.flatMap(w => w.items).count(i => i == Item.P)
  def totalFinishedProducts: Int = finishedProductsInBucket + finishedProductsOnConveyor + finishedProductsInHands
}

case class Worker(items: Set[Item], assemblyStage: Int) {
  
  override def toString: String = (items.mkString + (if (assemblyStage > 0) assemblyStage else " ")).padTo(3, ' ')
  
  def hasFinishedProduct: Boolean = items.contains(Item.P)
  
  def acceptsItem(item: Item): Boolean = Item.components.contains(item) && items.size < 2 && !items.contains(item)
  
  def assembleProduct: Worker =
    if (items == Item.components.toSet && assemblyStage < 3)
      Worker(items, assemblyStage + 1)
    else if (items == Item.components.toSet && assemblyStage == 3)
      Worker(Set(Item.P), 0)
    else this
}
object Worker {
  def empty: Worker = Worker(Set(), 0)
}

case class Slot(workers: Array[Worker], conveyorSlot: ConveyorSlot) {

  @tailrec
  private def actRecursive(conveyorSlot: ConveyorSlot,
                           updatedWorkers: Array[Worker],
                           availableWorkers: Array[Worker]): (ConveyorSlot, Array[Worker], Array[Worker]) = {
    if (availableWorkers.isEmpty) {
      (conveyorSlot, updatedWorkers, availableWorkers)
    } else {
      val (cs, uw, aw) = (conveyorSlot, updatedWorkers, availableWorkers.head, availableWorkers.tail) match {
        case (AvailableSlot(), result, worker, remainder) if worker.hasFinishedProduct =>
          (Product(), result :+ worker.copy(items = worker.items.filter(i => i != Item.P)), remainder)

        case (conveyor, result, Worker(items, 3), remainder) =>
          (conveyor, result :+ Worker(items = Set(Item.P), assemblyStage = 0), remainder)

        case (conveyor, result, Worker(items, stage), remainder) if items == Item.components.toSet =>
          (conveyor, result :+ Worker(items, stage + 1), remainder)

        case (Component(item), result, worker, remainder)
          if worker.acceptsItem(item)
            && (!worker.hasFinishedProduct || !remainder.exists(w => w.acceptsItem(item) && !w.hasFinishedProduct)) =>
          (UnavailableSlot(), result :+ worker.copy(items = worker.items + item), remainder)

        case (conveyor, result, worker, remainder) => (conveyor, result :+ worker, remainder)
      }
      actRecursive(cs, uw, aw)
    }
  }

  def act(): Slot = {
    val (updatedConveyorSlot, updatedWorkers, availableWorkers) = actRecursive(conveyorSlot, Array[Worker](), workers)
    Slot(updatedWorkers, updatedConveyorSlot)
  }
}
