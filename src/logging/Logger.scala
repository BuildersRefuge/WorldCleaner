package logging

import java.io._
import java.text.SimpleDateFormat
import java.util.{Date, SimpleTimeZone}

/**
  * Logger responsible for writing to out log file.
  */
object Logger {
  // Name of log file
  val fileName: String = "world-cleaner.log"

  /**
    * Get the current UTC time.
    *
    * @return string formatted UTC time.
    */
  def getCurrentTime: String = {
    val date = new Date()
    val timeformat = "yyyy-MM-dd HH:mm:ss"
    val format = new SimpleDateFormat(timeformat)
    format.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"))
    format.format(date)
  }

  /**
    * Write a log message to the logfile.
    *
    * @param m String message to log.
    * @param l LogLevel severity of the logged message.
    */
  def write(m: String, l: LogLevel.Value): Unit = {
    try {
      val file = new File(fileName)
      val bw = new BufferedWriter(new FileWriter(file, true))
      val out = new PrintWriter(bw)
      val outputString = getCurrentTime + " : " + "[" + l.toString + "]" + " -> " + m
      out.println(outputString)
      out.close()
      bw.close()
    }
    catch {
      case ioe: IOException => System.err.println("IOException: " + ioe.getMessage)
      case exception: Exception => System.err.println("Exception: " + exception.getMessage)
    }
  }

  /**
    * Log a info message.
    *
    * @param m String message to log.
    */
  def info(m: String): Unit = write(m, LogLevel.INFO)


  /**
    * Log a warning message.
    *
    * @param m String message to log.
    */
  def warning(m: String): Unit = write(m, LogLevel.WARNING)

  /**
    * Log a error message.
    *
    * @param m String message to log.
    */
  def error(m: String): Unit = write(m, LogLevel.ERROR)

  /**
    * Log a debug message
    *
    * @param m String message to log
    */
  def debug(m: String): Unit = write(m, LogLevel.DEBUG)
}
