package org.careerseekers.userservice.services

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class UsersServiceTest(private val usersService: UsersService) {
    @Test
    fun `should return correct age when birthday is in the past`() {
        val birthday = LocalDate.now().minusYears(25)
        val age = usersService.getUserAge(birthday)

        assertEquals(25, age)
    }

    @Test
    fun `should return null when birthday is in the future`() {
        val birthday = LocalDate.now().plusYears(1)
        val age = usersService.getUserAge(birthday)

        assertNull(age)
    }

    @Test
    fun `should return 0 when birthday is today`() {
        val birthday = LocalDate.now()
        val age = usersService.getUserAge(birthday)

        assertEquals(0, age)
    }
}