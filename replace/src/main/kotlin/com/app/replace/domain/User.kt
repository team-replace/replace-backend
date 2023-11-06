package com.app.replace.domain

import jakarta.persistence.*

@Entity
@Table(name = "users")
class User(
    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val password: String,

    @Column(unique = true, nullable = false)
    val connectionCode: String,

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
) : TemporalRecord()
