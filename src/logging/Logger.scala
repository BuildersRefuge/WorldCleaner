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
    * @param message  String message to log.
    * @param logLevel LogLevel severity of the logged message.
    */
  def write(message: String, logLevel: LogLevel.Value, printToConsole: Boolean = false): Unit = {
    try {
      val file = new File(fileName)
      val bw = new BufferedWriter(new FileWriter(file, true))
      val out = new PrintWriter(bw)
      val outputString = getCurrentTime + " : " + "[" + logLevel.toString + "]" + " -> " + message
      if (printToConsole) {
        System.out.println(outputString)
      }
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
    * @param message String message to log.
    */
  def info(message: String, printToConsole: Boolean = false): Unit = write(message, LogLevel.INFO, printToConsole)


  /**
    * Log a warning message.
    *
    * @param message String message to log.
    */
  def warning(message: String, printToConsole: Boolean = false): Unit = write(message, LogLevel.WARNING, printToConsole)

  /**
    * Log a error message.
    *
    * @param message String message to log.
    */
  def error(message: String, printToConsole: Boolean = false): Unit = write(message, LogLevel.ERROR, printToConsole)

  /**
    * Log a debug message
    *
    * @param message String message to log
    */
  def debug(message: String, printToConsole: Boolean = false): Unit = write(message, LogLevel.DEBUG, printToConsole)
}
