import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender

import static ch.qos.logback.classic.Level.*

appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n"
  }
  filter(ch.qos.logback.classic.filter.ThresholdFilter) {
    level = INFO
  }
}

appender("FILEOUT", FileAppender) {
  append = false
  def homeDir = System.getProperty("user.home")
  file = "${System.getProperty("app.home", homeDir)}/logs/bagger.log"
  encoder(PatternLayoutEncoder) {
    pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n"
  }
  filter(ch.qos.logback.classic.filter.ThresholdFilter) {
    level = INFO
  }
}

appender("DEBUGOUT", FileAppender) {
  append = false
  def homeDir = System.getProperty("user.home")
  file = "${System.getProperty("app.home", homeDir)}/logs/debug.log"
  encoder(PatternLayoutEncoder) {
    pattern = "%date %level [%thread] %logger{10} [%file:%line] %msg%n"
  }
  filter(ch.qos.logback.classic.filter.ThresholdFilter) {
    level = DEBUG
  }
}

root(DEBUG, ["STDOUT", "FILEOUT", "DEBUGOUT"])

//these are set to ERROR since they output unneeded logging at the info level
logger("org.springframework", ERROR)
