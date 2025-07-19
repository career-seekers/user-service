package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.UserDocuments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserDocsRepository : JpaRepository<UserDocuments, Long> {
    fun findByUserId(userId: Long): UserDocuments
}