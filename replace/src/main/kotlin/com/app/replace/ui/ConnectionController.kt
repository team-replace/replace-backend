package com.app.replace.ui

import com.app.replace.application.ConnectionService
import com.app.replace.ui.argumentresolver.Authenticated
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/connection")
class ConnectionController(
    val connectionService: ConnectionService
) {
    @GetMapping("/code")
    fun loadCode(@Authenticated userId: Long) : ResponseEntity<LoadingSingleConnectionCodeResponse> {
        return ResponseEntity
            .ok(LoadingSingleConnectionCodeResponse(connectionService.loadConnection(userId)))
    }

    @PostMapping("/code")
    fun makeConnection(
        @Authenticated userId: Long,
        @RequestBody connectionRequest: MakingConnectionRequest
    ) {
        connectionService.makeConnection(userId, connectionRequest.code)
    }
}

data class MakingConnectionRequest(val code: String)
data class LoadingSingleConnectionCodeResponse(val code : String)
