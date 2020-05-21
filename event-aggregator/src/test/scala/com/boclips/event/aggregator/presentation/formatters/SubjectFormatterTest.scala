package com.boclips.event.aggregator.presentation.formatters

import com.boclips.event.aggregator.domain.model.Subject
import com.boclips.event.aggregator.testsupport.Test

class SubjectFormatterTest extends Test {

  it should "write subject name" in {
    val json = SubjectFormatter.formatRow(Subject(name = "Maths"))

    json.getString("name") shouldBe "Maths"
  }

}
