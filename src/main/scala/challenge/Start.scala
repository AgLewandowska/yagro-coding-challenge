package challenge

object Start {
  def main(args: Array[String]): Unit = {
    val printSteps = args.map(s => s.toLowerCase).contains("printsteps")
    val noOfSteps = args
      .find(arg => arg.toLowerCase.startsWith("steps="))
      .map(steps => Integer.parseInt(steps.replace("steps=", "")))
      .getOrElse(100)
    val noOfSlots = args
      .find(arg => arg.toLowerCase.startsWith("slots="))
      .map(steps => Integer.parseInt(steps.replace("slots=", "")))
      .getOrElse(3)
    
    val factory = new Factory(printSteps, noOfSlots,
      State(
        finishedProductsInBucket = 0,
        conveyor = (1 to noOfSlots).map(_ => AvailableSlot()),
        workers = (1 to 2).map(_ => (1 to noOfSlots).map(_ => Worker.empty).toArray).toArray)
    )
    factory.run(noOfSteps)

    println(s"Run complete!")
    println(s"Finished products in bucket: ${factory.state.finishedProductsInBucket}")
    println(s"Finished products on conveyor: ${factory.state.finishedProductsOnConveyor}")
    println(s"Finished products in hands: ${factory.state.finishedProductsInHands}")
    println(s"Total: ${factory.state.totalFinishedProducts}")
  }
}