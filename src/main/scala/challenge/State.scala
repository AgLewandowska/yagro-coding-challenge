package challenge

case class State(finishedProducts: Int,
                 conveyor: Seq[ConveyorSlot],
                 workers: Array[Array[Worker]]) {
  
  override def toString: String = {
    s"Finished products: $finishedProducts\n" +
      workers(0).mkString("", " ", "\n") +
      conveyor.map { 
        case AvailableSlot() | UnavailableSlot() => "   "
        case Product() => Product().item.toString.padTo(3, ' ')
        case Component(item) => item.toString.padTo(3, ' ')
      }.mkString("", " ", "\n") +
      workers(1).mkString(" ")
  }
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
  def act(): Slot = {
    
    val (updatedConveyorSlot, updatedWorkers) = workers.foldLeft((conveyorSlot, Array[Worker]())){
      case ((AvailableSlot(), result), worker: Worker) if worker.items.contains(Item.P) =>
        (Product(), result :+ worker.copy(items = worker.items.filterNot(_ == Item.P)))

      case ((conveyor, result), Worker(items, 3)) =>
        (conveyor, result :+ Worker(items = Set(Item.P), assemblyStage = 0))
        
      case ((conveyor, result), Worker(items, stage)) if items == Item.components.toSet && stage < 3 =>
        (conveyor, result :+ Worker(items, stage + 1))
        
      case ((Component(item), result), worker: Worker) if worker.acceptsItem(item) =>
        (UnavailableSlot(), result :+ worker.copy(items = worker.items + item))
        
      case ((conveyor, result), in: Worker) => (conveyor, result :+ in)
    }
    
    Slot(updatedWorkers,
      updatedConveyorSlot match {
        case UnavailableSlot() => AvailableSlot() 
        case x => x
    })
  }
}
