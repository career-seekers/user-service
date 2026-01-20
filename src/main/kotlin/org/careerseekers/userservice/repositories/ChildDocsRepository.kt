package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.ChildDocuments
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface ChildDocsRepository : JpaRepository<ChildDocuments, Long>, JpaSpecificationExecutor<ChildDocuments> {
    fun findByChildId(childId: Long): ChildDocuments?
    fun findBySnilsNumber(snilsNumber: String): ChildDocuments?
}