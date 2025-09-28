package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.Children
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChildrenRepository : JpaRepository<Children, Long> {
    fun findByUserId(userId: Long): List<Children>
}