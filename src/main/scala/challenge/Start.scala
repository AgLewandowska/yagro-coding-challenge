package challenge

object Start {
  def main(args: Array[String]): Unit = {
    val printSteps = args.contains("printSteps")

    val initialState = State(finishedProducts = 0,
      conveyor = Seq(None, None, None),
      workers = Seq((Worker.empty, Worker.empty),
        (Worker.empty, Worker.empty),
        (Worker.empty, Worker.empty))
    )

    var step = 0
    var state = initialState

    while(step < 100) {
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