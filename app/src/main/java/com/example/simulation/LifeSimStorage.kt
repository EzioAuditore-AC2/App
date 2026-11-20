package com.example.simulation

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class LifeSimStorage(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun hasSavedProgress(): Boolean = prefs.contains(KEY_SAVED_STATE)

    fun saveState(state: LifeSimState) {
        val root = JSONObject().apply {
            put("lifestyle", state.lifestyle.name)
            put("dayNumber", state.dayNumber)
            put("timeOfDay", state.timeOfDay.name)
            put("playerX", state.playerX.toDouble())
            put("playerY", state.playerY.toDouble())
            put("energy", state.energy)
            put("calm", state.calm)
            put("creativity", state.creativity)
            put("connection", state.connection)
            put("gardenBloomStage", state.gardenBloomStage)
            put("studioArtworksCount", state.studioArtworksCount)
            put("booksReadCount", state.booksReadCount)
            put("teaBreaksCount", state.teaBreaksCount)
            put("pondReflectionsCount", state.pondReflectionsCount)
            put("totalActivitiesDone", state.totalActivitiesDone)
            put("hasShownCompletionForCurrentRun", state.hasShownCompletionForCurrentRun)

            val appObj = JSONObject().apply {
                put("name", state.appearance.name)
                put("skinToneIndex", state.appearance.skinToneIndex)
                put("hairStyle", state.appearance.hairStyle.name)
                put("hairColorIndex", state.appearance.hairColorIndex)
                put("clothingStyle", state.appearance.clothingStyle.name)
                put("outfitColorIndex", state.appearance.outfitColorIndex)
                put("accessory", state.appearance.accessory.name)
            }
            put("appearance", appObj)

            val npcObj = JSONObject()
            state.npcTalkCounts.forEach { (k, v) -> npcObj.put(k, v) }
            put("npcTalkCounts", npcObj)

            put("unlockedActivityIds", JSONArray(state.unlockedActivityIds.toList()))
            put("completedLifestyles", JSONArray(state.completedLifestyles.map { it.name }))

            val journalArr = JSONArray()
            state.journalEntries.take(20).forEach { entry ->
                journalArr.put(
                    JSONObject().apply {
                        put("id", entry.id)
                        put("dayNumber", entry.dayNumber)
                        put("timeLabel", entry.timeLabel)
                        put("title", entry.title)
                        put("note", entry.note)
                    }
                )
            }
            put("journalEntries", journalArr)
        }
        prefs.edit().putString(KEY_SAVED_STATE, root.toString()).apply()
    }

    fun loadState(): LifeSimState? {
        val raw = prefs.getString(KEY_SAVED_STATE, null) ?: return null
        return runCatching {
            val root = JSONObject(raw)
            val lifestyle = runCatching {
                DreamLifestyle.valueOf(root.optString("lifestyle", DreamLifestyle.CREATIVE_CAREER.name))
            }.getOrDefault(DreamLifestyle.CREATIVE_CAREER)

            val timeOfDay = runCatching {
                TimeOfDay.valueOf(root.optString("timeOfDay", TimeOfDay.MORNING.name))
            }.getOrDefault(TimeOfDay.MORNING)

            val appObj = root.optJSONObject("appearance")
            val appearance = if (appObj != null) {
                CharacterAppearance(
                    name = appObj.optString("name", "Maya"),
                    skinToneIndex = appObj.optInt("skinToneIndex", 1).coerceIn(0, SimPalette.skinTones.lastIndex),
                    hairStyle = runCatching { HairStyle.valueOf(appObj.optString("hairStyle")) }.getOrDefault(HairStyle.SOFT_BOB),
                    hairColorIndex = appObj.optInt("hairColorIndex", 1).coerceIn(0, SimPalette.hairColors.lastIndex),
                    clothingStyle = runCatching { ClothingStyle.valueOf(appObj.optString("clothingStyle")) }.getOrDefault(ClothingStyle.COZY_SWEATER),
                    outfitColorIndex = appObj.optInt("outfitColorIndex", 0).coerceIn(0, SimPalette.outfitColors.lastIndex),
                    accessory = runCatching { AccessoryStyle.valueOf(appObj.optString("accessory")) }.getOrDefault(AccessoryStyle.HEADPHONES)
                )
            } else CharacterAppearance()

            val npcCounts = mutableMapOf<String, Int>()
            root.optJSONObject("npcTalkCounts")?.let { obj ->
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    npcCounts[key] = obj.optInt(key, 0)
                }
            }

            val unlockedIds = mutableSetOf<String>()
            root.optJSONArray("unlockedActivityIds")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val id = arr.optString(i)
                    if (id.isNotBlank()) unlockedIds.add(id)
                }
            }

            val completedLifestyles = mutableSetOf<DreamLifestyle>()
            root.optJSONArray("completedLifestyles")?.let { arr ->
                for (i in 0 until arr.length()) {
                    runCatching { DreamLifestyle.valueOf(arr.optString(i)) }.getOrNull()?.let {
                        completedLifestyles.add(it)
                    }
                }
            }

            val journal = mutableListOf<SimJournalEntry>()
            root.optJSONArray("journalEntries")?.let { arr ->
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    journal.add(
                        SimJournalEntry(
                            id = obj.optString("id", "j_$i"),
                            dayNumber = obj.optInt("dayNumber", 1),
                            timeLabel = obj.optString("timeLabel", "Morning"),
                            title = obj.optString("title", "Reflection"),
                            note = obj.optString("note", "")
                        )
                    )
                }
            }

            val loaded = LifeSimState(
                phase = SimPhase.INTRO,
                hasSavedSession = true,
                appearance = appearance,
                lifestyle = lifestyle,
                dayNumber = root.optInt("dayNumber", 1).coerceAtLeast(1),
                timeOfDay = timeOfDay,
                playerX = root.optDouble("playerX", 0.48).toFloat().coerceIn(0.08f, 0.92f),
                playerY = root.optDouble("playerY", 0.50).toFloat().coerceIn(0.14f, 0.88f),
                energy = root.optInt("energy", 90).coerceIn(10, 100),
                calm = root.optInt("calm", 60).coerceIn(10, 100),
                creativity = root.optInt("creativity", 55).coerceIn(10, 100),
                connection = root.optInt("connection", 50).coerceIn(10, 100),
                gardenBloomStage = root.optInt("gardenBloomStage", 0).coerceIn(0, 4),
                studioArtworksCount = root.optInt("studioArtworksCount", 0).coerceAtLeast(0),
                booksReadCount = root.optInt("booksReadCount", 0).coerceAtLeast(0),
                teaBreaksCount = root.optInt("teaBreaksCount", 0).coerceAtLeast(0),
                pondReflectionsCount = root.optInt("pondReflectionsCount", 0).coerceAtLeast(0),
                totalActivitiesDone = root.optInt("totalActivitiesDone", 0).coerceAtLeast(0),
                npcTalkCounts = npcCounts,
                unlockedActivityIds = unlockedIds,
                completedLifestyles = completedLifestyles,
                journalEntries = journal,
                hasShownCompletionForCurrentRun = root.optBoolean("hasShownCompletionForCurrentRun", false)
            )
            loaded.copy(milestones = LifeSimEngine.evaluateMilestones(loaded))
        }.getOrNull()
    }

    fun clearSavedProgress() {
        prefs.edit().remove(KEY_SAVED_STATE).apply()
    }

    companion object {
        private const val PREFS_NAME = "neurotalks_life_sim_local"
        private const val KEY_SAVED_STATE = "life_sim_state_json"
    }
}
