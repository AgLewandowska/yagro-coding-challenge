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
        case Product() => currentState.finishedProducts + 1
        case _ => currentState.finishedProducts
      },
      conveyor = generateComponent.map(i => Component(i)).getOrElse(AvailableSlot()) +: currentState.conveyor.dropRight(1)
    )
  }

  def doAction(initialState: State): State = {
    val (workers, conveyor) = initialState.workers.transpose
      .zip(initialState.conveyor)
      .map { case (workers: Array[Worker], conveyorItem) => Slot(workers, conveyorItem) }
      .map(s => s.act())
      .map(s => (s.workers, s.conveyorSlot))
      .unzip
    initialState.copy(
      conveyor = conveyor.toSeq.map(c => if (c == UnavailableSlot()) AvailableSlot() else c),
      workers = workers.transpose)
  }
}
