package challenge

import challenge.Item.*

import scala.util.Random

class Factory(printSteps: Boolean, noOfSlots: Int, var state: State) {
  
  def run(noOfSteps: Int): Unit = {
    for (step <- 1 to noOfSteps) {
      
        moveConveyor()
        if (printSteps) {
          print("\u001b[2J")
          println(s"Step $step")
          println(s"Moved conveyor")
          println(state)
          Thread.sleep(500)
        }
        
        doAction()
        if (printSteps) {
          print("\u001b[2J")
          println(s"Step $step")
          println(s"Action done")
          println(state)
          Thread.sleep(500)
        }
    }
  }

  def moveConveyor(): Unit = {
    state = state.copy(
      finishedProductsInBucket = state.conveyor.last match {
        case Product() => state.finishedProductsInBucket + 1
        case _ => state.finishedProductsInBucket
      },
      conveyor = Factory.generateComponent
        .map(i => Component(i))
        .getOrElse(AvailableSlot())
        +: state.conveyor.dropRight(1).map(c => if (c == UnavailableSlot()) AvailableSlot() else c)
    )
  }

  def doAction(): Unit = {
    val (workers, conveyor) = state.workers.transpose
      .zip(state.conveyor)
      .map { case (workers: Array[Worker], conveyorItem) => Slot(workers, conveyorItem) }
      .map(s => s.act())
      .map(s => (s.workers, s.conveyorSlot))
      .unzip
    state = state.copy(
      conveyor = conveyor,
      workers = workers.transpose)
  }
}

object Factory {
  private def generateComponent: Option[Item] = {
    val componentGenerator = new Random()
    componentGenerator.nextInt(Item.components.length + 1) match {
      case 0 => None
      case c => Some(Item.fromOrdinal(c - 1))
    }
  }
}
