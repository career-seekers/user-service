package org.careerseekers.userservice.repositories

import org.careerseekers.userservice.entities.Users
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsersRepository : JpaRepository<Users, Long> {
    fun getByEmail(email: String): Users?
    fun getByMobileNumber(mobileNumber: String): Users?
}