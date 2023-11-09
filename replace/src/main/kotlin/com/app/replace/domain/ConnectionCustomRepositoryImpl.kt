package com.app.replace.domain

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class ConnectionCustomRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate
) : ConnectionCustomRepository {
    override fun existsConnectionHavingId(id: Long): Boolean {
        val count = jdbcTemplate.queryForObject(
            "select count(*) from connection where host_id = ? or partner_id = ? limit 1", Long::class.java, id, id
        )
        return count != 0L
    }
}
