package challenge

class FactoryRunner(printSteps: Boolean, noOfSlots: Int) {
  
  def run(noOfSteps: Int): Unit = {
    val initialState = State(finishedProducts = 0,
      conveyor = (1 to noOfSlots).map(_ => AvailableSlot()),
      workers = (1 to 2).map(_ => (1 to noOfSlots).map(_ => Worker.empty).toArray).toArray
    )

    var step = 1
    var state = initialState

    while (step <= noOfSteps) {
      if (printSteps) {
        state = Factory.moveConveyor(state)
        print("\u001b[2J")
        println(s"Step $step")
        println(s"Moved conveyor")
        println(state)
        Thread.sleep(1000)

        state = Factory.doAction(state)
        print("\u001b[2J")
        println(s"Step $step")
        println(s"Action done")
        println(state)
        Thread.sleep(1000)
      } else {
        state = Factory.moveConveyor(state)
        state = Factory.doAction(state)
      }
      step = step + 1
    }
    if (!printSteps) {
      println(s"Finished products: ${state.finishedProducts}")
    }
  }
}
