package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.TelegramLinks
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelegramLinksRepository : JpaRepository<TelegramLinks, Long> {
    fun getByTgLink(link: String): TelegramLinks?
}