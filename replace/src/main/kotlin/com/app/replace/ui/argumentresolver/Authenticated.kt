package com.app.replace.ui.argumentresolver

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Authenticated(
    val required: Boolean = true
)
