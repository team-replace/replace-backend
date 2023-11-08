package com.app.replace.ui.exception

class UnAuthenticatedException : RuntimeException() {
    override val message: String?
        get() = super.message
}
