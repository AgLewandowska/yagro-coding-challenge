package challenge

class Worker(val items: Set[Item],
             val assemblyStage: Int) {

  override def toString: String = (items.mkString + (if (assemblyStage > 0) assemblyStage else " ")).padTo(3, ' ')
  def hasFinishedProduct: Boolean = items.contains(Item.P)
  def acceptsItem(item: Item): Boolean = Item.components.contains(item) && items.size < 2 && !items.contains(item)

  def assembleProduct(): Worker =
    if (items == Item.components.toSet && assemblyStage < 3)
      Worker(items, assemblyStage + 1)
    else if (items == Item.components.toSet && assemblyStage == 3)
      Worker(Set(Item.P), 0)
    else this

  def placeProduct(): Worker =
    Worker(items.filterNot(i => i == Item.P), assemblyStage)

  def pickUpItem(item: Item): Worker =
    Worker(items + item, assemblyStage)
}

object Worker {
  def empty: Worker = Worker(Set(), 0)
}