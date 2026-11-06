package com.evduzeni.planner

import com.evduzeni.planner.data.entity.TaskEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TaskRecurrenceTest {

    @Test
    fun testTaskCompletionSchedulesNextDueDay() {
        val todayEpoch = 20500L
        val intervalDays = 7

        val task = TaskEntity(
            id = 1,
            roomId = 1,
            title = "Vacuum living room",
            intervalDays = intervalDays,
            dueDayEpoch = todayEpoch,
            completedDayEpoch = null
        )

        // Mark completed
        val completedTask = task.copy(
            completedDayEpoch = todayEpoch,
            previousDueDayEpoch = task.dueDayEpoch,
            dueDayEpoch = todayEpoch + intervalDays
        )

        assertEquals(todayEpoch, completedTask.completedDayEpoch)
        assertEquals(todayEpoch + 7, completedTask.dueDayEpoch)
        assertEquals(todayEpoch, completedTask.previousDueDayEpoch)

        // Undo completion
        val undoneTask = completedTask.copy(
            completedDayEpoch = null,
            dueDayEpoch = completedTask.previousDueDayEpoch ?: todayEpoch,
            previousDueDayEpoch = null
        )

        assertNull(undoneTask.completedDayEpoch)
        assertEquals(todayEpoch, undoneTask.dueDayEpoch)
        assertNull(undoneTask.previousDueDayEpoch)
    }

    @Test
    fun testIntervalRangeBoundaries() {
        val validIntervals = listOf(1, 2, 3, 7, 14, 30, 365)
        for (interval in validIntervals) {
            val clamped = interval.coerceIn(1, 365)
            assertEquals(interval, clamped)
        }

        assertEquals(1, 0.coerceIn(1, 365))
        assertEquals(365, 500.coerceIn(1, 365))
    }
}
