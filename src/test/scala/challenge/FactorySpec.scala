package challenge

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class FactorySpec extends AnyFlatSpec with should.Matchers {
  "Running" should
    "result in an average of 28 to 32 finished products when running for 100 steps" in {
    val results = for (n <- 1 to 10) yield {
      val factory = new Factory(false, 3, State.emptyWithNSlots(3))
      factory.run(100)
      factory.state.totalFinishedProducts
    }

    (results.sum.toDouble / 10.0) should be (25.0 +- 5.0)
  }
  
  "Moving the conveyor" should
    "move an item" in {
      val factory = new Factory(false, 3,
        State(
          finishedProductsInBucket = 0,
          componentsInBucket = Map(),
          conveyor = Seq(AvailableSlot(), Component(Item.A), AvailableSlot()),
          workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray
        ))
      factory.moveConveyor()

      factory.state.conveyor(1) shouldBe AvailableSlot()
      factory.state.conveyor(2) shouldBe Component(Item.A)
    }
  it should "append new component item or None" in {
    val factory = new Factory(false, 3, State.emptyWithNSlots(3))
    for (n <- 1 to 10) {
      factory.moveConveyor()
      factory.state.conveyor.head should be(AvailableSlot()).or(be(Component(Item.A))).or(be(Component(Item.B)))
    }
  }
  it should "drop last item" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 0,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), Component(Item.A)), 
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
    )
    factory.moveConveyor()

    factory.state.conveyor(1) shouldEqual AvailableSlot()
    factory.state.conveyor(2) shouldEqual AvailableSlot()
  }
  it should "retain length of 3" in {
    val factory = new Factory(false, 3, State.emptyWithNSlots(3))
    factory.moveConveyor()

    factory.state.conveyor should have length 3
    factory.state.workers(0) should have length 3
    factory.state.workers(1) should have length 3
  }
  it should "count finished item before dropping it" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), Product()), 
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
    )
    factory.moveConveyor()

    factory.state.finishedProductsInBucket shouldEqual 11
  }
  it should "count component before dropping it" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(Item.A -> 5, Item.B -> 2),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), Component(Item.A)),
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
    )
    factory.moveConveyor()

    factory.state.componentsInBucket(Item.A) shouldEqual 6
    factory.state.componentsInBucket(Item.B) shouldEqual 2
  }
  it should "count component if component bucket is empty" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), Component(Item.B)),
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
    )
    factory.moveConveyor()

    factory.state.componentsInBucket(Item.B) shouldEqual 1
  }
  it should "reset slot availability" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 0,
        componentsInBucket = Map(),
        conveyor = Seq(UnavailableSlot(), UnavailableSlot(), UnavailableSlot()),
        workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
    )
    factory.moveConveyor()

    factory.state.conveyor(1) should be(AvailableSlot())
    factory.state.conveyor(2) should be(AvailableSlot())
  }

  "Doing an action" should
    "move an A item to worker one if worker one is empty" in {
      val factory = new Factory(false, 3,
        State(
          finishedProductsInBucket = 10,
          componentsInBucket = Map(),
          conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()), 
          workers = (1 to 2).map(_ => (1 to 3).map(_ => Worker.empty).toArray).toArray)
      )

      factory.doAction()
      factory.state.workers(0)(0).items should contain(Item.A)
      factory.state.conveyor(0) shouldBe UnavailableSlot()
      factory.state.workers(1)(0).items shouldBe empty
  }
  it should "move an A item to worker one if worker one has a B item" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(Component(Item.B), AvailableSlot(), AvailableSlot()), 
        workers = Array(
          Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty), 
          (1 to 3).map(_ => Worker.empty).toArray))
    )

    factory.doAction()
    factory.state.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    factory.state.conveyor(0) shouldBe UnavailableSlot()
  }
  it should "not move an A item if workers have an A item" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()), 
        workers = Array(
          Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty), 
          Array(Worker(Set(Item.A), 0), Worker.empty, Worker.empty)))
    )

    factory.doAction()
    factory.state.workers(0)(0).items should contain(Item.A)
    factory.state.workers(1)(0).items should contain(Item.A)
    factory.state.conveyor(0) shouldBe Component(Item.A)
  }
  it should "not move an A item to worker one if worker one has A and B items" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()), 
        workers = Array(
          Array(Worker(Set(Item.A, Item.B), 0), Worker.empty, Worker.empty), 
          Array(Worker(Set(Item.A, Item.B), 0), Worker.empty, Worker.empty)))
    )

    factory.doAction()
    factory.state.conveyor(0) shouldBe Component(Item.A)
  }
  it should "move an A item to worker two if worker one has a P item and worker two accepts an A item" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
        workers = Array(
          Array(Worker(Set(Item.P), 0), Worker.empty, Worker.empty),
          Array(Worker(Set(Item.B), 0), Worker.empty, Worker.empty)))
    )

    factory.doAction()
    factory.state.conveyor(0) shouldBe UnavailableSlot()
    factory.state.workers(0)(0).items should not contain (Item.A)
    factory.state.workers(1)(0).items should contain (Item.A)
  }
  it should "construct a P item if worker has A and B items in stage equal to final assembly stage" in {
    val factory = new Factory(false, 3, 
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()), 
        workers = Array(
          Array(Worker(Set(Item.A, Item.B), Factory.finalAssemblyStage), Worker.empty, Worker.empty),
          (1 to 3).map(_ => Worker.empty).toArray))
    )

    factory.doAction()
    factory.state.workers(0)(0).items shouldEqual Set(Item.P)
  }
  it should "increment assembly stage if worker has items A and B" in {
    val factory = new Factory(false, 3, 
      State(finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()), 
        workers = Array(
          Array(Worker(Set(Item.A, Item.B), 1), Worker.empty, Worker.empty), 
          (1 to 3).map(_ => Worker.empty).toArray))
    )

    factory.doAction()
    factory.state.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    factory.state.workers(0)(0).assemblyStage shouldBe 2
  }
  it should "place a finished product if conveyor slot is available" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(AvailableSlot(), AvailableSlot(), AvailableSlot()),
        workers = Array(
          (1 to 3).map(_ => Worker.empty).toArray,
          Array(Worker(Set(Item.P), 0), Worker.empty, Worker.empty)))
    )

    factory.doAction()
    factory.state.workers(0)(0).items shouldBe empty
    factory.state.workers(1)(0).items shouldBe empty
    factory.state.conveyor(0) shouldBe Product()
  }
  it should "not place a finished product if conveyor slot has been interacted with" in {
    val factory = new Factory(false, 3,
      State(
        finishedProductsInBucket = 10,
        componentsInBucket = Map(),
        conveyor = Seq(Component(Item.A), AvailableSlot(), AvailableSlot()),
        workers = Array(
          Array(Worker(Set(Item.B), 0), Worker.empty, Worker.empty),
          Array(Worker(Set(Item.P), 0), Worker.empty, Worker.empty)))
    )

    factory.doAction()
    factory.state.workers(0)(0).items shouldBe Set(Item.A, Item.B)
    factory.state.workers(1)(0).items shouldBe Set(Item.P)
    factory.state.conveyor(0) shouldBe UnavailableSlot()
  }
}