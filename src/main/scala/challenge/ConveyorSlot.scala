package challenge

sealed trait ConveyorSlot {
  def item: Option[Item]
}

case class Component(private val someItem: Item) extends ConveyorSlot {
  override def item: Option[Item] = Some(someItem)
}
case class Product() extends ConveyorSlot {
  override def item: Option[Item] = Some(Item.P)
}
case class AvailableSlot() extends ConveyorSlot {
  override def item: Option[Item] = None
}
case class UnavailableSlot() extends ConveyorSlot {
  override def item: Option[Item] = None
}