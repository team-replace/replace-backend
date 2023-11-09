package com.app.replace.domain

interface ConnectionCustomRepository {
    fun existsConnectionHavingId(id: Long): Boolean
}
