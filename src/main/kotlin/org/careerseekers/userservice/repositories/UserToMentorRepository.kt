package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.ChildToMentor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserToMentorRepository : JpaRepository<ChildToMentor, Long>
