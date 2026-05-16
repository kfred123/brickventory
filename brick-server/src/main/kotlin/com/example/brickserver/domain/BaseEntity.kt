package com.example.brickserver.domain

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import java.time.Instant
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp

@MappedSuperclass
abstract class BaseEntity {
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null

    @UpdateTimestamp @Column(name = "updated_at", nullable = false) var updatedAt: Instant? = null
}
