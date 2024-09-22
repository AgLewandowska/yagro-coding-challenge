package challenge

object Start {
  def main(args: Array[String]): Unit = {
    val printSteps = args.contains("printSteps")
    val noOfSteps = args
      .find(arg => arg.toLowerCase.startsWith("steps="))
      .map(steps => Integer.parseInt(steps.replace("steps=", "")))
      .getOrElse(100)
    val noOfSlots = args
      .find(arg => arg.toLowerCase.startsWith("slots="))
      .map(steps => Integer.parseInt(steps.replace("slots=", "")))
      .getOrElse(3)
    
    val factoryRunner = new FactoryRunner(printSteps, noOfSlots)
    factoryRunner.run(noOfSteps)
  }
}