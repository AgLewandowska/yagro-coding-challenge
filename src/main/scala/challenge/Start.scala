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
    
    val factory = new Factory(printSteps, noOfSlots, State.emptyWithNSlots(noOfSlots))
    factory.run(noOfSteps)

    println(s"Run complete!")
    println(s"Components in bucket: ${if (factory.state.componentsInBucket.isEmpty) "None" else factory.state.componentsInBucket.mkString(", ")}")
    println(s"Finished products in bucket: ${factory.state.finishedProductsInBucket}")
    println(s"Finished products on conveyor: ${factory.state.finishedProductsOnConveyor}")
    println(s"Finished products in hands: ${factory.state.finishedProductsInHands}")
    println(s"Total: ${factory.state.totalFinishedProducts}")
  }
}