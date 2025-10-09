package com.example.campeonatocatarinense.data.xml

import android.content.Context
import android.util.Xml
import com.example.campeonatocatarinense.data.model.Championship
import com.example.campeonatocatarinense.data.model.Match
import com.example.campeonatocatarinense.data.model.Round
import com.example.campeonatocatarinense.data.model.Team
import org.xmlpull.v1.XmlPullParser

object ChampionshipParser {
    fun loadFromAssets(context: Context, fileName: String = "campeonato.xml"): Championship {
        context.assets.open(fileName).use { input ->
            val parser = Xml.newPullParser().apply { setInput(input, null) }
            var event = parser.eventType

            val teams = mutableListOf<Team>()
            val rounds = mutableListOf<Round>()
            var currentRound: Round? = null
            var champName = "Campeonato"
            var year = 0

            while (event != XmlPullParser.END_DOCUMENT) {
                if (event == XmlPullParser.START_TAG) {
                    when (parser.name) {
                        "campeonato" -> {
                            champName = parser.getAttributeValue(null, "name") ?: champName
                            year = parser.getAttributeValue(null, "year")?.toIntOrNull() ?: year
                        }
                        "team" -> teams.add(
                            Team(
                                id = parser.getAttributeValue(null, "id").toInt(),
                                name = parser.getAttributeValue(null, "name") ?: "Team"
                            )
                        )
                        "round" -> {
                            currentRound = Round(parser.getAttributeValue(null, "number").toInt())
                            rounds.add(currentRound!!)
                        }
                        "match" -> {
                            currentRound?.matches?.add(
                                Match(
                                    id = parser.getAttributeValue(null, "id"),
                                    teamA = parser.getAttributeValue(null, "teamA").toInt(),
                                    teamB = parser.getAttributeValue(null, "teamB").toInt(),
                                    goalsA = parser.getAttributeValue(null, "goalsA")
                                        ?.toIntOrNull(),
                                    goalsB = parser.getAttributeValue(null, "goalsB")
                                        ?.toIntOrNull(),
                                    location = parser.getAttributeValue(null, "location"),
                                    date = parser.getAttributeValue(null, "date")
                                )
                            )
                        }
                    }
                }
                event = parser.next()
            }
            return Championship(year, champName, teams, rounds)
        }
    }
}
