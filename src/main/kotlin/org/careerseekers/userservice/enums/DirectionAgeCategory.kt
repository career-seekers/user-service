package org.careerseekers.userservice.enums

import org.careerseekers.userservice.exceptions.BadRequestException

enum class DirectionAgeCategory(private val ageAlias: String) {
    PRESCHOOL_1("4-5 лет"),
    PRESCHOOL_2("6-7 лет"),
    SCHOOL_1("6-8 лет"),
    SCHOOL_2("9-11 лет"),
    SCHOOL_3("12-13 лет");

    companion object {
        fun getAgeCategory(age: Int, learningClass: Short): DirectionAgeCategory {
            return when (age) {
                in 4..5 -> PRESCHOOL_1
                in 6..7 if learningClass == 0.toShort() -> PRESCHOOL_2
                in 6..8 if learningClass != 0.toShort() -> SCHOOL_1
                in 9..11 -> SCHOOL_2
                in 12..13 -> SCHOOL_3
                else -> throw BadRequestException("Данный возраст не поддерживается.")
            }
        }

        fun DirectionAgeCategory.getAgeAlias() = this.ageAlias
    }
}