package models

class DisposalConfig {
  /**
    *
    */
  var newerThen: String = "2018-10-07"
  var olderThen: String = "2018-10-07"
  var largerThen: Int = 20000
  var smallerThen: Int = 20000

  var sizeFilterActive: Boolean = false
  var dateFilterActive: Boolean = false
  var ignoreModified: Boolean = false
  var autoDelete: Boolean = false
  var mustMatchAllConditions: Boolean = true
}
