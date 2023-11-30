package com.app.replace.ui

import com.app.replace.application.ConnectionService
import com.app.replace.ui.argumentresolver.Authenticated
import com.app.replace.ui.request.MakingConnectionRequest
import com.app.replace.ui.response.LoadingSingleConnectionCodeResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/abort")
    fun disconnect(@Authenticated userId: Long) {
        connectionService.disconnect(userId)
    }
}

