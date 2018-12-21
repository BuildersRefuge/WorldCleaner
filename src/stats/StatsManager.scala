package stats

import java.util.concurrent.TimeUnit

object StatsManager {
  private var startTime: Long = _
  private var stopTime: Long = _
  private var checkpoints: Array[(String, Long)] = Array[(String, Long)]()
  private var counters: Array[(String, Int)] = Array[(String, Int)]()

  def start(): Unit = {
    this.startTime = System.nanoTime()
  }

  def stop(): Unit = {
    this.stopTime = System.nanoTime()
  }

  def createCheckpoint(name: String): Unit = {
    checkpoints = checkpoints :+ (name, System.nanoTime())
  }

  def addCounter(name: String, initialValue: Int = 0): Unit = {
    counters = counters :+ (name, initialValue)
  }

  def increaseCounter(name: String): Unit = {
    val counterIndex: Int = this.counters.indexWhere(p => p._1.equals(name))
    if (counterIndex < 0) {
      this.addCounter(name, 1)
    } else {
      val counter: (String, Int) = this.counters(counterIndex)
      this.counters.update(counterIndex, (counter._1, counter._2 + 1))
    }
  }

  override def toString: String = {
    val builder: StringBuilder = StringBuilder.newBuilder
    val elapsedMilli = stopTime - startTime
    builder.append(s"Elapsed time : ${getDurationBreakdown(elapsedMilli / 1000000)}\n")
    builder.append("---------CHECKPOINTS---------\n")
    for (i <- checkpoints.indices) {
      val checkpoint: (String, Long) = checkpoints(i)
      if (i == 0) {
        val duration: Long = checkpoint._2 - startTime
        builder.append(s"${checkpoint._1}: ${getDurationBreakdown(duration / 1000000)}\n")
      } else {
        val previousCheckpoint: (String, Long) = checkpoints(i - 1)
        val checkpoint: (String, Long) = checkpoints(i)
        val duration: Long = checkpoint._2 - previousCheckpoint._2
        builder.append(s"${checkpoint._1}: ${getDurationBreakdown(duration / 1000000)}\n")
      }
    }
    builder.append("---------COUNTERS---------\n")
    for (i <- counters.indices) {
      val counter: (String, Int) = counters(i)
      builder.append(s"${counter._1}: ${counter._2}\n")
    }
    builder.toString()
  }

  private def getDurationBreakdown(duration: Long): String = {
    var millis = duration
    if (millis < 0) return "Duration must be greater than zero!"
    val days: Long = TimeUnit.MILLISECONDS.toDays(millis)
    millis -= TimeUnit.DAYS.toMillis(days)
    val hours: Long = TimeUnit.MILLISECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
    millis -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis)
    millis -= TimeUnit.SECONDS.toMillis(seconds)
    val sb: StringBuilder = new StringBuilder(64)
    if (days != 0) {
      sb.append(days)
      sb.append(" Days ")
    }
    if (hours != 0) {
      sb.append(hours)
      sb.append(" Hours ")
    }
    if (minutes != 0) {
      sb.append(minutes)
      sb.append(" Minutes ")
    }
    if (seconds != 0) {
      sb.append(seconds)
      sb.append(" Seconds ")
    }
    sb.append(millis)
    sb.append(" Milliseconds")
    sb.toString
  }
}
