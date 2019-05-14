package com.boclips.event.service.testsupport

import com.boclips.events.types.User
import com.boclips.events.types.UserActivated
import com.boclips.events.types.video.VideosSearched

object TestFactories {

    fun createUser(userId: String = "user-1", userEmail: String = "user@example.com"): User {
        return User.builder().id(userId).email(userEmail).build()
    }

    fun createUserActivated(userId: String = "user-1", userEmail: String = "user@example.com"): UserActivated {
        return UserActivated.builder()
                .user(User.builder().id(userId).email(userEmail).build())
                .totalUsers(100)
                .activatedUsers(50)
                .build()
    }

    fun createVideosSearched(pageIndex: Int = 0, pageSize: Int = 10, query: String = "a great video", totalResults: Int = 14, user: User = createUser()): VideosSearched {
        return VideosSearched
                .builder()
                .pageIndex(pageIndex)
                .pageSize(pageSize)
                .query(query)
                .totalResults(totalResults)
                .user(user)
                .build()
    }
}
