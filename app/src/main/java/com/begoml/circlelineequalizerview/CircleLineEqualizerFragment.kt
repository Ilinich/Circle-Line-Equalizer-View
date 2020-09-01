package com.begoml.circlelineequalizerview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.equalizr_fragment.*

class CircleLineEqualizerFragment : Fragment(R.layout.equalizr_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radioPageEqualizer.apply {
//            setViewBackgroundResource(R.color.equalizer_bg)
            //setBackgroundResource(R.color.equalizer_bg)
        }

        radioPagePlayControlPlay.setOnClickListener {
            playPause(true)
        }

        radioPagePlayControlStop.setOnClickListener {
            playPause(false)
        }
    }

    private fun playPause(isPlaying: Boolean) {
        radioPagePlayControlStop.visibility(isPlaying)
        radioPagePlayControlPlay.visibility(!isPlaying)

        if (isPlaying) {
            radioPageEqualizer.onStart()
        } else {
            radioPageEqualizer.onStop()
        }

    }
}

fun View.visibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}