package challenge

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class WorkerSpec extends AnyFlatSpec with should.Matchers {
  "hasFinishedProduct" should "return true if worker is holding a product" in {
    val worker = Worker(Set(Item.P), 0)

    worker.hasFinishedProduct should be(true)
  }
  it should "return true if worker is holding a product and another item" in {
    val worker = Worker(Set(Item.P, Item.A), 0)

    worker.hasFinishedProduct should be(true)
  }
  it should "return false if worker is not holding anything" in {
    val worker = Worker(Set(), 0)

    worker.hasFinishedProduct should be(false)
  }
  it should "return false if worker is not holding a product" in {
    val worker = Worker(Set(Item.A, Item.B), 0)

    worker.hasFinishedProduct should be(false)
  }

  "acceptsItem" should "accept item A if it is empty" in {
    val worker = Worker.empty

    worker.acceptsItem(Item.A) should be(true)
  }
  it should "accept item A if it is holding item B" in {
    val worker = Worker(Set(Item.B), 0)

    worker.acceptsItem(Item.A) should be(true)
  }
  it should "accept item A if it is holding a product" in {
    val worker = Worker(Set(Item.P), 0)

    worker.acceptsItem(Item.A) should be(true)
  }
  it should "not accept item A if it is holding two items already" in {
    val worker = Worker(Set(Item.A, Item.B), 0)

    worker.acceptsItem(Item.A) should be(false)
  }

  "assembling a product" should "increment assembly stage if the worker is holding items A and B and the stage is less than 3" in {
    val worker = Worker(Set(Item.A, Item.B), 0)

    worker.assembleProduct().assemblyStage shouldBe 1
  }
  it should "assemble product if the worker is holding items A and B and the stage is 3" in {
    val worker = Worker(Set(Item.A, Item.B), 3)

    worker.assembleProduct().assemblyStage shouldBe 0
    worker.assembleProduct().items shouldBe Set(Item.P)
  }
  it should "not increment assembly stage if the worker is not holding items A and B" in {
    val worker = Worker(Set(Item.A), 0)

    worker.assembleProduct().assemblyStage shouldBe 0
  }
}
