package challenge

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class StateSpec extends AnyFlatSpec with should.Matchers {
  "Counting the total products" should "include products on conveyor" in {
    State(0,
      Seq(Product(), Component(Item.A), Component(Item.A), Product(), AvailableSlot()),
      (1 to 2).map(_ => (1 to 5).map(_ => Worker.empty).toArray).toArray
    ).totalFinishedProducts should be(2)
  }
  it should "include products in hands" in {
    State(0,
      Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
      Array(
        Array(Worker(Set(Item.A, Item.P), 0), Worker(Set(Item.A), 0), Worker(Set(Item.A, Item.B), 2)),
        Array(Worker(Set(Item.P), 0), Worker(Set(Item.B), 0), Worker(Set(), 0)))
    ).totalFinishedProducts should be(2)
  }
  it should "include products in bucket" in {
    State(5,
      Seq(Component(Item.A), Component(Item.B), AvailableSlot()),
      Array(
        Array(Worker(Set(Item.A), 0), Worker(Set(Item.A), 0), Worker(Set(Item.A, Item.B), 2)),
        Array(Worker(Set(Item.B), 0), Worker(Set(Item.B), 0), Worker(Set(), 0)))
    ).totalFinishedProducts should be(5)
  }
}