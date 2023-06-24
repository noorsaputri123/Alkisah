package com.rie.alkisah

import com.rie.alkisah.database.db.MrKisahResRoom

object DataDummy {
    fun generateDummyStoryResponse(): List<MrKisahResRoom> {
        val items: MutableList<MrKisahResRoom> = arrayListOf()
        val ranges = 0..10
        for (i in ranges) {
            val stories = MrKisahResRoom(
                "id + $i",
                "photo + $i",
                "createdAt + $i",
                "name + $i",
                "description + $i",
                0.0 + i,
                0.0 + i
            )
            items.add(stories)
        }
        return items
    }
}