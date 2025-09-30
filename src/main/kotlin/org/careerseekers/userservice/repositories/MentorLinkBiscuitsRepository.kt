package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.MentorLinkBiscuits
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MentorLinkBiscuitsRepository : JpaRepository<MentorLinkBiscuits, Long> {
    fun findByUserId(userId: Long): MentorLinkBiscuits?
    fun findByBiscuit(biscuit: String): MentorLinkBiscuits?
}