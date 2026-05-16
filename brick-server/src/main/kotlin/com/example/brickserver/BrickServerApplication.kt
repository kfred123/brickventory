package com.example.brickserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BrickServerApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            runApplication<BrickServerApplication>(*args)
        }
    }
}
