package com.app.replace.application

import com.app.replace.domain.CODE_MAX_LENGTH
import com.app.replace.domain.User
import com.app.replace.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val connectionService: ConnectionService
) {
    fun createUser(
        email: String, password: String, nickname: String
    ): Long {
        val connectionCode = UUID.randomUUID().toString()
            .replace("-", "")
            .substring(0, CODE_MAX_LENGTH)
        return userRepository.save(User(email, nickname, password, connectionCode)).id
    }

    fun loadSimpleUserInformationById(userId: Long): SimpleUserInformation {
        val user =
            userRepository.findByIdOrNull(userId)
                ?: throw IllegalArgumentException("식별자에 해당하는 유저 정보를 찾을 수 없습니다.")
        return SimpleUserInformation(
            user.nickname,
            "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"
        )
    }

    fun loadUserInformationWithPartner(userId: Long): UserInformation {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw IllegalArgumentException("식별자에 해당하는 유저 정보를 찾을 수 없습니다.")

        val partnerId = connectionService.findPartnerIdByUserId(userId)

        if (Objects.nonNull(partnerId)) {
            val partner = userRepository.findByIdOrNull(partnerId)
                ?: throw IllegalArgumentException("식별자에 해당하는 유저 정보를 찾을 수 없습니다.")
            return UserInformationWithPartner(
                SimpleUserInformation(user.nickname, "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"),
                SimpleUserInformation(partner.nickname, "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png")
            )
        }

        return AloneUserInformation(SimpleUserInformation(user.nickname, "https://replace-s3.s3.ap-northeast-2.amazonaws.com/client/profile/replace-default-profile-image.png"))
    }
}

data class SimpleUserInformation(val nickname: String, val imageUrl: String)

abstract class UserInformation {
    abstract val user: SimpleUserInformation
}

data class AloneUserInformation(override val user: SimpleUserInformation) : UserInformation()

data class UserInformationWithPartner(
    override val user: SimpleUserInformation,
    val partner: SimpleUserInformation
) : UserInformation()

