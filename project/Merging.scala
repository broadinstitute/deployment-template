import sbtassembly.{MergeStrategy, PathList}

object Merging {
  // noop merge strategy - a placeholder for if/when we need a real merge strategy
  def customMergeStrategy(oldStrategy: String => MergeStrategy):String => MergeStrategy = {
    case x => oldStrategy(x)
  }
}
