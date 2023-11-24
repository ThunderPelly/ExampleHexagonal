package com.example.hexagonal.adapter.`in`.model

import org.jetbrains.annotations.NotNull

data class TaskRequestDto(@NotNull("Die Aufgabenbeschreibung darf nicht null oder leer sein") val description: String?) {
    constructor() : this(null)
}