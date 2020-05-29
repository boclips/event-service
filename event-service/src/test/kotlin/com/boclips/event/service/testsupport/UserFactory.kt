package com.boclips.event.service.testsupport

import com.boclips.event.service.testsupport.SubjectFactory.createSubjects
import com.boclips.eventbus.domain.user.Organisation
import com.boclips.eventbus.domain.user.User
import com.boclips.eventbus.domain.user.UserProfile
import java.time.ZonedDateTime

object UserFactory {

    fun createUser(
        id: String = "user-1",
        createdAt: ZonedDateTime = ZonedDateTime.now(),
        email: String? = null,
        profile: UserProfile = createUserProfile(),
        organisation: Organisation? = null,
        isBoclipsEmployee: Boolean = false
    ): User {
        return User.builder()
            .id(id)
            .email(email)
            .createdAt(createdAt)
            .isBoclipsEmployee(isBoclipsEmployee)
            .organisation(organisation)
            .profile(profile)
            .build()
    }

    fun createUserProfile(
        firstName: String? = null,
        lastName: String? = null,
        subjectNames: List<String> = emptyList(),
        ages: List<Int> = emptyList(),
        role: String? = null,
        school: Organisation? = null,
        hasOptedIntoMarketing: Boolean? = null
    ): UserProfile {
        return UserProfile.builder()
            .firstName(firstName)
            .lastName(lastName)
            .subjects(createSubjects(subjectNames))
            .ages(ages)
            .school(school)
            .role(role)
                .hasOptedIntoMarketing(hasOptedIntoMarketing)
            .build()
    }
}
