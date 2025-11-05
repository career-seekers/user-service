package org.careerseekers.userservice.utils.storages

import org.careerseekers.userservice.dto.statistics.UsersStatisticPairDto

object StatisticsStorage {
    var tutorsInfo: UsersStatisticPairDto = UsersStatisticPairDto(0, 0)
        private set

    var expertsInfo: UsersStatisticPairDto = UsersStatisticPairDto(0, 0)
        private set

    var mentorsInfo: UsersStatisticPairDto = UsersStatisticPairDto(0, 0)
        private set

    var usersInfo: UsersStatisticPairDto = UsersStatisticPairDto(0, 0)
        private set

    var childrenCount: Int = 0
        private set

    fun setTutorsInfo(info: UsersStatisticPairDto) = apply { tutorsInfo = info }
    fun setExpertsInfo(info: UsersStatisticPairDto) = apply { expertsInfo = info }
    fun setMentorsInfo(info: UsersStatisticPairDto) = apply { mentorsInfo = info }
    fun setUsersInfo(info: UsersStatisticPairDto) = apply { usersInfo = info }
    fun setChildrenCount(count: Int) = apply { childrenCount = count }
}