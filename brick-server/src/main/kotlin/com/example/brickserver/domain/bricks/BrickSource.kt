package com.example.brickserver.domain.bricks

enum class BrickSource(val displayName: String) {
    LEGO("Lego"),
    BLUEBRIXX("BlueBrixx");

    companion object {
        fun fromPath(value: String): BrickSource? =
                entries.find { it.name.equals(value, ignoreCase = true) }
    }
}
