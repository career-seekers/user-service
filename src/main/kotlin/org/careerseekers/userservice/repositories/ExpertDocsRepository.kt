package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.ExpertDocuments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpertDocsRepository : JpaRepository<ExpertDocuments, Long> {
    fun findByUserId(userId: Long): ExpertDocuments?
}