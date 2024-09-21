package challenge

case class State(finishedProducts: Int,
                 conveyor: Seq[Option[Item]],
                 workers: Seq[(Worker, Worker)]) {
  override def toString: String = {
    val lines = workers.map(_._1)
      .lazyZip(workers.map(_._2))
      .lazyZip(conveyor)
      .unzip3
    s"Finished products: $finishedProducts\n" +
      workers.map(_._1).mkString(" ") + "\n" +
      conveyor.map { 
        case None => "   "
        case Some(item) => item.toString.padTo(3, ' ')
      }.mkString(" ") + "\n" +
      workers.map(_._2).mkString(" ")
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

case class Slot(workerOne: Worker, workerTwo: Worker, conveyorItem: Option[Item])
