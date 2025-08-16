package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.UserToMentor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserToMentorRepository : JpaRepository<UserToMentor, Long>
