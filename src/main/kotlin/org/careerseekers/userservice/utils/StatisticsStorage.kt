package org.careerseekers.userservice.utils

object StatisticsStorage {
    var tutorsInfo: Pair<Int, Int> = Pair(0, 0)
        private set

    var expertsInfo: Pair<Int, Int> = Pair(0, 0)
        private set

    var mentorsInfo: Pair<Int, Int> = Pair(0, 0)
        private set

    var usersInfo: Pair<Int, Int> = Pair(0, 0)
        private set

    var childrenCount: Int = 0
        private set

    fun setTutorsInfo(info: Pair<Int, Int>) = apply { tutorsInfo = info }
    fun setExpertsInfo(info: Pair<Int, Int>) = apply { expertsInfo = info }
    fun setMentorsInfo(info: Pair<Int, Int>) = apply { mentorsInfo = info }
    fun setUsersInfo(info: Pair<Int, Int>) = apply { usersInfo = info }
    fun setChildrenCount(count: Int) = apply { childrenCount = count }
}