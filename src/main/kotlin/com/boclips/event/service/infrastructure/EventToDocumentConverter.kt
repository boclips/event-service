package com.boclips.event.service.infrastructure

import com.boclips.events.types.UserActivated
import org.bson.Document

object EventToDocumentConverter {

    fun convertUserActivated(userActivated: UserActivated): Document {
        return Document(mapOf<String, Any>(
                "type" to "USER_ACTIVATED",
                "userId" to userActivated.user.id,
                "userIsBoclips" to userActivated.user.email.endsWith("@boclips.com"),
                "timestamp" to userActivated.timestamp,
                "totalUsers" to userActivated.totalUsers,
                "activatedUsers" to userActivated.activatedUsers
        ))
    }

}
