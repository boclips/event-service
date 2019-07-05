package com.boclips.event.service.domain

interface VideoRepository {

    fun saveVideo(id: String, title: String, contentPartnerName: String)
}
