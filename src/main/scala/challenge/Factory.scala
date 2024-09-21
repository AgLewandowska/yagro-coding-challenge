package challenge

import challenge.Item._

import scala.util.Random

class Factory

object Factory {

  private def generateComponent: Option[Item] = {
    val componentGenerator = new Random()
    componentGenerator.nextInt(Item.components.length + 1) match {
      case 0 => None
      case c => Some(Item.fromOrdinal(c - 1))
    }
  }

  def moveConveyor(currentState: State): State = {
    currentState.copy(
      finishedProducts = currentState.conveyor.last match {
        case Some(Item.P) => currentState.finishedProducts + 1
        case _ => currentState.finishedProducts
      },
      conveyor = generateComponent +: currentState.conveyor.dropRight(1)
    )
  }

  def doAction(initialState: State): State = {
    val (workers, conveyor) = initialState.workers.map(_._1)
      .lazyZip(initialState.workers.map(_._2))
      .lazyZip(initialState.conveyor)
      .map { case (workerOne, workerTwo, conveyorItem) => Slot(workerOne, workerTwo, conveyorItem) }
      .map {
        case Slot(Worker(hasItemP, _, false), workerTwo, None) if hasItemP.contains(Item.P) =>
          Slot(Worker(hasItemP.filterNot(i => i == Item.P), 0, true), workerTwo, Some(Item.P))

        case Slot(workerOne, Worker(hasItemP, _, false), None) if hasItemP.contains(Item.P) =>
          Slot(workerOne, Worker(hasItemP.filterNot(i => i == Item.P), 0, true), Some(Item.P))

        case Slot(acceptsConveyorItem, workerTwo, Some(conveyorItem)) if acceptsConveyorItem.acceptsItem(conveyorItem) =>
          Slot(acceptsConveyorItem.copy(items = acceptsConveyorItem.items + conveyorItem, done = true), workerTwo, None)

        case Slot(workerOne, acceptsConveyorItem, Some(conveyorItem)) if acceptsConveyorItem.acceptsItem(conveyorItem) =>
          Slot(workerOne, acceptsConveyorItem.copy(items = acceptsConveyorItem.items + conveyorItem, done = true), None)

        case x => x
      }
      .map(s => s.copy(workerOne = s.workerOne.assembleProduct, workerTwo = s.workerTwo.assembleProduct))
      .map(s => s.copy(workerOne = s.workerOne.copy(done = false), workerTwo = s.workerTwo.copy(done = false)))
      .map(s => ((s.workerOne, s.workerTwo), s.conveyorItem))
      .unzip
    initialState.copy(conveyor = conveyor, workers = workers)
  }
}
