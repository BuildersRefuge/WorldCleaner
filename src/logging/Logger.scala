package logging

import java.io._
import java.text.SimpleDateFormat
import java.util.{Date, SimpleTimeZone}

/**
  * Logger responsible for writing to out log file.
  */
object Logger {

  val fileName: String = "world-cleaner.log"

  /**
    * Get the current UTC time.
    *
    * @return string formatted UTC time.
    */
  def getCurrentTime: String = {
    val date = new Date()
    val format = new SimpleDateFormat()
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
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    val outputString = getCurrentTime() + " : " + "[" + l.toString + "]" + " -> " + m
    bw.write(outputString)
    bw.close()
  }

  /**
    * Log a info message.
    *
    * @param m String message to log.
    */
  def info(m: String): Unit = {
    write(m, LogLevel.INFO)
  }

  /**
    * Log a warning message.
    *
    * @param m String message to log.
    */
  def warning(m: String): Unit = {
    write(m, LogLevel.WARNING)
  }

  /**
    * Log a error message.
    *
    * @param m String message to log.
    */
  def error(m: String): Unit = {
    write(m, LogLevel.ERROR)
  }
}
