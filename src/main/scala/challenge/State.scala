package challenge

case class State(finishedProducts: Int,
                 conveyor: Seq[Option[Item]],
                 workers: Array[Array[Worker]]) {
  
  override def toString: String = {
    s"Finished products: $finishedProducts\n" +
      workers(0).mkString("", " ", "\n") +
      conveyor.map { 
        case None => "   "
        case Some(item) => item.toString.padTo(3, ' ')
      }.mkString("", " ", "\n") +
      workers(1).mkString(" ")
  }
}

case class Worker(items: Set[Item], assemblyStage: Int, done: Boolean) {
  
  override def toString: String = (items.mkString + (if (assemblyStage > 0) assemblyStage else " ")).padTo(3, ' ')
  
  def hasFinishedProduct: Boolean = items.contains(Item.P)
  
  def acceptsItem(item: Item): Boolean = Item.components.contains(item) && items.size < 2 && !items.contains(item)
  
  def assembleProduct: Worker =
    if (!done && items == Item.components.toSet && assemblyStage < 3)
      Worker(items, assemblyStage + 1, true)
    else if (!done && items == Item.components.toSet && assemblyStage == 3)
      Worker(Set(Item.P), 0, true)
    else this
}
object Worker {
  def empty: Worker = Worker(Set(), 0, false)
}

case class Slot(workers: Array[Worker], conveyorItem: Option[Item]) {
  def act(): Slot = {
    
    val (interacted, updatedConveyorItem, updatedWorkers) = workers.foldLeft((false, conveyorItem, Array[Worker]())){
      case ((false, None, result), in: Worker) if in.items.contains(Item.P) =>
        (true, Some(Item.P), result :+ in.copy(items = in.items.filterNot(_ == Item.P)))
        
      case ((interacted, conveyor, result), in: Worker) if in.items == Item.components.toSet && in.assemblyStage < 3 =>
        (interacted, conveyor, result :+ in.copy(assemblyStage = in.assemblyStage + 1))
        
      case ((interacted, conveyor, result), in: Worker) if in.items == Item.components.toSet && in.assemblyStage == 3 =>
        (interacted, conveyor, result :+ in.copy(items = Set(Item.P), assemblyStage = 0))
        
      case ((false, Some(item), result), in: Worker) if in.acceptsItem(item) =>
        (true, None, result :+ in.copy(items = in.items + item))
        
      case ((interacted, conveyor, result), in: Worker) => (interacted, conveyor, result :+ in)
    }
    
    Slot(updatedWorkers, updatedConveyorItem)
  }
}
