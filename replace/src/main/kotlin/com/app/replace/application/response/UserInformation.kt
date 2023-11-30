package com.app.replace.application.response

abstract class UserInformation {
    abstract val user: SimpleUserProfile
}

data class AloneUserInformation(override val user: SimpleUserProfile) : UserInformation()

data class UserInformationWithPartner(
    override val user: SimpleUserProfile,
    val partner: SimpleUserProfile
) : UserInformation()
