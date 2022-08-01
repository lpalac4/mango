package com.moraware.mango.databinding

import android.text.Editable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by luis palacios on 7/26/17.
 */

class TextViewBindingAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter("formattedDate")
        fun setFormattedDate(view: TextInputEditText, time: ZonedDateTime?) {
            val formattedString = time?.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, hh:mm:ss a")) ?: ""
            view.text = Editable.Factory.getInstance().newEditable(formattedString)
        }

        @JvmStatic
        @BindingAdapter("countDownText")
        fun setCountDownText(view: TextView, time: Date?){
            if(time == null) {
                view.visibility = View.GONE
                return
            }

            timeUntil(view, time.time)
        }

        @JvmStatic
        @BindingAdapter("timeSince")
        fun setTimeSince(view: TextView, time: Long?){
            if(time == null) {
                view.visibility = View.GONE
                return
            }

            timeSince(view, time)
        }

        private fun timeUntil(textView: TextView, time: Long) {
            var countDownText = ""
            var days: String
            var hours: String
            var min: String

            val preparedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
            var diff = Duration.between(ZonedDateTime.now(), preparedTime)

            val daysCount = diff.toDays()
            days = daysCount.toString()
            diff = diff.minusDays(daysCount)

            val hoursCount = diff.toHours()
            hours = hoursCount.toString()
            diff = diff.minusHours(hoursCount)

            val minCount = diff.toMinutes()
            min = minCount.toString()
            diff = diff.minusMinutes(minCount)

            val secCount = diff.toMillis()

            //TODO: LOCALIZE
            if(daysCount == 1L) countDownText += "$days day " else if(daysCount > 1) countDownText += "$days days "
            if(hoursCount == 1L) countDownText += "$hours hour " else if(hoursCount > 1) countDownText += "$hours hours "
            if(daysCount == 0L) {
                if (minCount == 1L) countDownText += "$min min" else if (minCount > 1) countDownText += "$min min"
            }

            if(daysCount == 0L && hoursCount == 0L && minCount == 0L && secCount != 0L) countDownText = "< 1 min"

            textView.text = countDownText
        }

        private fun timeSince(textView: TextView, time: Long) {
            var countDownText = ""
            var days: String
            var hours: String
            var min: String

            val preparedTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault())
            var diff = Duration.between(preparedTime, ZonedDateTime.now())

            val daysCount = diff.toDays()
            days = daysCount.toString()
            diff = diff.minusDays(daysCount)

            val hoursCount = diff.toHours()
            hours = hoursCount.toString()
            diff = diff.minusHours(hoursCount)

            val minCount = diff.toMinutes()
            min = minCount.toString()
            diff = diff.minusMinutes(minCount)

            val secCount = diff.toMillis()

            //TODO: LOCALIZE
            if(daysCount == 1L) countDownText += "$days day " else if(daysCount > 1) countDownText += "$days days "
            if(hoursCount == 1L) countDownText += "$hours hour " else if(hoursCount > 1) countDownText += "$hours hours "
            if(daysCount == 0L) {
                if (minCount == 1L) countDownText += "$min min" else if (minCount > 1) countDownText += "$min min"
            }

            if(daysCount == 0L && hoursCount == 0L && minCount == 0L && secCount != 0L) countDownText = "< 1 min"

            textView.text = countDownText
        }
    }

}
