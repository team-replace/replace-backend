package com.app.replace.domai

import com.app.replace.domain.Connection
import com.app.replace.domain.ConnectionCustomRepository
import com.app.replace.domain.ConnectionId
import org.springframework.data.jpa.repository.JpaRepository

interface ConnectionRepository : JpaRepository<Connection, ConnectionId>, ConnectionCustomRepository
