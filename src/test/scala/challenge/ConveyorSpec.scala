package challenge

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class ConveyorSpec extends AnyFlatSpec with should.Matchers {
  "The Conveyor" should
    "move an item" in {
      val initialState = State(finishedProducts = 0,
        conveyor = Seq(AvailableSlot(), Component(Item.A), AvailableSlot()),
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
      )
      val result = Factory.moveConveyor(initialState)

      result.conveyor(1) shouldBe AvailableSlot()
      result.conveyor(2) shouldBe Component(Item.A)
    }
  it should "append new component item or None" in {
    val initialState = State(finishedProducts = 0,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()),
      workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
    )
    val result = Factory.moveConveyor(initialState)

    result.conveyor(0) should be(AvailableSlot()).or(be(Component(Item.A))).or(be(Component(Item.B)))
  }
  it should "drop last item" in {
    val initialState = State(finishedProducts = 0,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), Component(Item.A)),
      workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
    )
    val result = Factory.moveConveyor(initialState)

    result.conveyor(1) shouldEqual AvailableSlot()
    result.conveyor(2) shouldEqual AvailableSlot()
  }
  it should "retain length of 3" in {
    val initialState = State(finishedProducts = 0,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()),
      workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
    )
    val result = Factory.moveConveyor(initialState)

    result.conveyor should have length 3
    result.workers(0) should have length 3
    result.workers(1) should have length 3
  }
  it should "count finished item before dropping it" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), Product()),
      workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
    )
    val result = Factory.moveConveyor(initialState)

    result.finishedProducts shouldEqual 11
  }

  "Acting" should
    "move an A item to worker one if worker one is empty" in {
      val initialState = State(finishedProducts = 10,
        conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
      )

      val result = Factory.doAction(initialState)
      result.workers(0)(0).items should contain(Item.A)
      result.conveyor(0) shouldBe AvailableSlot()
      result.workers(1)(0).items shouldBe empty
  }
  it should "move an A item to worker one if worker one has a B item" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(Component(Item.B), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty),
        (1 to 3).map(_ => Worker.empty).toArray)
    )

    val result = Factory.doAction(initialState)
    result.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    result.conveyor(0) shouldBe AvailableSlot()
  }
  it should "not move an A item if workers have an A item" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty),
        Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty))
    )

    val result = Factory.doAction(initialState)
    result.workers(0)(0).items should contain(Item.A)
    result.workers(1)(0).items should contain(Item.A)
    result.conveyor(0) shouldBe Component(Item.A)
  }
  it should "not move an A item to worker one if worker one has A and B items" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.A, Item.B), 0), Worker.empty, Worker.empty),
        Array(Worker(Set(Item.A, Item.B), 0), Worker.empty, Worker.empty))
    )

    val result = Factory.doAction(initialState)
    result.conveyor(0) shouldBe Component(Item.A)
  }
  it should "construct a P item if worker has A and B items in stage 3" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.A, Item.B), 3), Worker.empty, Worker.empty),
        (1 to 3).map(_ => Worker.empty).toArray)
    )

    val result = Factory.doAction(initialState)
    result.workers(0)(0).items shouldEqual Set(Item.P)
  }
  it should "increment assembly stage if worker has items A and B" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.A, Item.B), 1), Worker.empty, Worker.empty),
        (1 to 3).map(_ => Worker.empty).toArray)
    )

    val result = Factory.doAction(initialState)
    result.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    result.workers(0)(0).assemblyStage shouldBe 2
  }
  it should "not place a finished product if conveyor slot has been interacted with" in {
    val initialState = State(finishedProducts = 10,
      conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
      workers = Array(
        Array(Worker(Set(Item.B), 0), Worker.empty, Worker.empty),
        Array(Worker(Set(Item.P), 0), Worker.empty, Worker.empty))
    )

    val result = Factory.doAction(initialState)
    result.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    result.workers(1)(0).items shouldBe Set(Item.P)
    result.conveyor(0) shouldBe AvailableSlot()
  }
}