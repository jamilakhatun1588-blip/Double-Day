package com.doubleday.examclock

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : Activity() {
    private val startDate = LocalDate.of(2026, 11, 1)
    private val startTime = LocalTime.of(5, 0)
    private val firstCutoff = LocalTime.of(13, 30)
    private val endTime = LocalTime.of(22, 0)
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var root: LinearLayout
    private lateinit var realDate: TextView
    private lateinit var virtualDay: TextView
    private lateinit var phase: TextView
    private lateinit var countdown: TextView
    private lateinit var progress: TextView
    private lateinit var quote: TextView

    private val tick = object : Runnable {
        override fun run() {
            updateScreen()
            handler.postDelayed(this, 30_000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(18, 59, 48)
        window.navigationBarColor = Color.rgb(18, 59, 48)
        buildUi()
        updateScreen()
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(tick)
        handler.post(tick)
    }

    override fun onPause() {
        handler.removeCallbacks(tick)
        super.onPause()
    }

    private fun buildUi() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(24), dp(22), dp(24), dp(20))
            setBackgroundColor(Color.rgb(246, 248, 246))
        }

        val scroll = android.widget.ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dp(12))
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        content.addView(text("DOUBLE DAY", 14, "#176B52", true).apply {
            letterSpacing = 0.16f
        })
        content.addView(text("Your time. Your challenge.", 27, "#142A22", true).apply {
            setPadding(0, dp(8), 0, dp(5))
        })
        content.addView(text("Turn 60 real days into 120 focused virtual days.", 14, "#52645B", false))

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(18), dp(24), dp(18), dp(24))
            setBackgroundColor(Color.rgb(18, 59, 48))
        }
        val cardParams = LinearLayout.LayoutParams(-1, -2).apply {
            topMargin = dp(22)
            bottomMargin = dp(16)
        }
        card.addView(text("YOUR CURRENT VIRTUAL DATE", 12, "#B9D8C9", true).apply {
            gravity = Gravity.CENTER
        })
        virtualDay = text("DAY 1", 42, "#FFFFFF", true).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(6))
        }
        card.addView(virtualDay)
        phase = text("Morning block · 5:00 AM – 1:30 PM", 14, "#D8E9E0", false).apply {
            gravity = Gravity.CENTER
        }
        card.addView(phase)
        content.addView(card, cardParams)

        content.addView(sectionTitle("REAL-WORLD CLOCK"))
        realDate = text("", 17, "#142A22", true)
        content.addView(realDate)

        content.addView(sectionTitle("TIME UNTIL NEXT VIRTUAL DAY"))
        countdown = text("", 17, "#176B52", true)
        content.addView(countdown)

        content.addView(sectionTitle("CHALLENGE PROGRESS"))
        progress = text("", 15, "#142A22", true)
        content.addView(progress)

        quote = text("", 14, "#52645B", false).apply {
            setPadding(0, dp(22), 0, dp(8))
        }
        content.addView(quote)

        val note = text(
            "Schedule: 5:00 AM–1:30 PM = one virtual day; 1:30 PM–10:00 PM = the next. " +
                "10:00 PM–5:00 AM is the overnight rest window. The app follows your phone's local time.",
            12, "#65736B", false
        )
        note.setPadding(0, dp(12), 0, dp(8))
        content.addView(note)

        setContentView(root)
    }

    private fun updateScreen() {
        val now = LocalDateTime.now()
        val dateFmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy · h:mm:ss a", Locale.ENGLISH)
        realDate.text = now.format(dateFmt)

        val start = LocalDateTime.of(startDate, startTime)
        val challengeEnd = start.plusDays(60)
        val elapsed = Duration.between(start, now).seconds
        val totalSeconds = Duration.between(start, challengeEnd).seconds
        val inChallenge = !now.isBefore(start) && now.isBefore(challengeEnd)

        val today = now.toLocalDate()
        val time = now.toLocalTime()

        if (now.isBefore(start)) {
            virtualDay.text = "STARTS SOON"
            phase.text = "Begins 1 November 2026 at 5:00 AM"
            countdown.text = formatDuration(Duration.between(now, start).seconds) + " until launch"
            progress.text = "0 / 120 virtual days"
            quote.text = "Prepare your desk, plan your subjects, and begin on time."
            return
        }

        val realDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, today)
        val slot: Int
        val nextChange: LocalDateTime
        val phaseText: String
        val dayNumber: Long

        if (time.isBefore(startTime)) {
            // Overnight rest window: the next study block is the morning block.
            dayNumber = realDays * 2 + 1
            nextChange = LocalDateTime.of(today, startTime)
            slot = 0
            phaseText = "Rest window · next block starts at 5:00 AM"
        } else if (time.isBefore(firstCutoff)) {
            dayNumber = realDays * 2 + 1
            nextChange = LocalDateTime.of(today, firstCutoff)
            slot = 1
            phaseText = "Block 1 · 5:00 AM – 1:30 PM"
        } else if (time.isBefore(endTime)) {
            dayNumber = realDays * 2 + 2
            nextChange = LocalDateTime.of(today, endTime)
            slot = 2
            phaseText = "Block 2 · 1:30 PM – 10:00 PM"
        } else {
            dayNumber = realDays * 2 + 3
            nextChange = LocalDateTime.of(today.plusDays(1), startTime)
            slot = 0
            phaseText = "Rest window · next block starts tomorrow at 5:00 AM"
        }

        virtualDay.text = if (inChallenge) "DAY ${dayNumber.coerceIn(1, 120)}" else
            if (now >= challengeEnd) "CHALLENGE COMPLETE" else "DAY $dayNumber"
        phase.text = phaseText
        countdown.text = formatDuration(Duration.between(now, nextChange).seconds) + " until next change"

        // Count completed virtual days from the study-window schedule, not raw elapsed
        // clock hours: the overnight 10 PM–5 AM rest window must not advance progress.
        val completed = when {
            now >= challengeEnd -> 120
            now.isBefore(start) -> 0
            else -> (dayNumber - 1).toInt().coerceIn(0, 120)
        }
        val percent = ((completed / 120.0) * 100).toInt()
        progress.text = "$completed / 120 virtual days · $percent% of the 60-day challenge"
        quote.text = when {
            now >= challengeEnd -> "You reached the end of your planned timeline. Review what you learned and your exam readiness."
            slot == 0 -> "Rest is part of preparation. Protect your sleep so tomorrow's study counts."
            else -> "Do the next useful task. Consistency beats panic—focus on learning, practice, and revision."
        }
    }

    private fun formatDuration(secondsRaw: Long): String {
        val seconds = secondsRaw.coerceAtLeast(0)
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", h, m, s)
    }

    private fun sectionTitle(value: String): TextView =
        text(value, 12, "#65736B", true).apply {
            setPadding(0, dp(18), 0, dp(7))
        }

    private fun text(value: String, size: Int, color: String, bold: Boolean): TextView =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(Color.parseColor(color))
            if (bold) setTypeface(typeface, Typeface.BOLD)
            includeFontPadding = true
        }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
