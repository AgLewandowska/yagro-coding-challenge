package challenge

import challenge.Item.*

import scala.annotation.tailrec
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
        +: state
        .conveyor
        .dropRight(1)
        .map(c => if (c == UnavailableSlot()) AvailableSlot() else c)
    )
  }

  def doAction(): Unit = {
    val (conveyor, workers) = state.conveyor
      .zip(state.workers.transpose)
      .map { case (conveyorSlot, workers) =>
        Factory.actOnColumnRecursive(conveyorSlot, Array.empty, workers)
      }.map { case (conveyorSlot, workers: Array[Worker], remainingWorkers: Array[Worker]) =>
        (conveyorSlot, workers)
      }.unzip
    state = state.copy(
      conveyor = conveyor,
      workers = workers.toArray.transpose)
  }
}

object Factory {
  val finalAssemblyStage = 2

  private def generateComponent: Option[Item] = {
    Random().nextInt(Item.components.length + 1) match {
      case 0 => None
      case c => Some(Item.fromOrdinal(c - 1))
    }
  }

  @tailrec
  private def actOnColumnRecursive(conveyorSlot: ConveyorSlot,
                                   updatedWorkers: Array[Worker],
                                   availableWorkers: Array[Worker]): (ConveyorSlot, Array[Worker], Array[Worker]) = {
    if (availableWorkers.isEmpty) {
      (conveyorSlot, updatedWorkers, availableWorkers)
    } else {

      val worker = availableWorkers.head
      val remainingWorkers = availableWorkers.tail

      conveyorSlot match {
        case AvailableSlot()
          if worker.hasFinishedProduct =>
          actOnColumnRecursive(
            Product(),
            updatedWorkers :+ worker.placeProduct(),
            remainingWorkers
          )
        case Component(item)
          if worker.acceptsItem(item)
            && !(worker.hasFinishedProduct
            && remainingWorkers.exists(w => w.acceptsItem(item) && !w.hasFinishedProduct)) =>
          actOnColumnRecursive(
            UnavailableSlot(),
            updatedWorkers :+ worker.pickUpItem(item),
            remainingWorkers
          )
        case _ =>
          actOnColumnRecursive(
            conveyorSlot,
            updatedWorkers :+ worker.assembleProduct(),
            remainingWorkers
          )
      }
    }
  }
}
