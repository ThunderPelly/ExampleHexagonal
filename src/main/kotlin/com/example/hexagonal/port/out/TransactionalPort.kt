package com.example.hexagonal.port.out

interface TransactionalPort {
    fun beginTransaction()
    fun commitTransaction()
    fun rollbackTransaction()
}