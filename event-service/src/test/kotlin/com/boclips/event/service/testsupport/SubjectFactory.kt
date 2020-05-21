package com.boclips.event.service.testsupport

import com.boclips.eventbus.domain.Subject
import com.boclips.eventbus.domain.SubjectId

object SubjectFactory {

    fun createSubjects(names: List<String>): List<Subject> {
        return names.map {
            Subject
                .builder()
                .id(SubjectId("id-$it"))
                .name(it)
                .build()
        }
    }

}
