package com.example.campeonatocatarinense.data.model

data class Team(val id: Int, val name: String)

data class Match(
    val id: String,
    val teamA: Int,
    val teamB: Int,
    var goalsA: Int? = null,
    var goalsB: Int? = null,
    var location: String? = null,
    var date: String? = null
)

data class Round(
    val number: Int,
    val matches: MutableList<Match> = mutableListOf()
)

data class Championship(
    val year: Int,
    val name: String,
    val teams: List<Team>,
    val rounds: List<Round>
)
