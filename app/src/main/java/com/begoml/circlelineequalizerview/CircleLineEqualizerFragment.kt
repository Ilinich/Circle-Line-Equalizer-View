package com.begoml.circlelineequalizerview

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.equalizr_fragment.*

class CircleLineEqualizerFragment : Fragment(R.layout.equalizr_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        equalizerView.setViewBackgroundResource(R.color.equalizer_bg)

        playControlPlay.setOnClickListener {
            playPause(true)
        }

        playControlStop.setOnClickListener {
            playPause(false)
        }

        equalizerView.setViewBackgroundResource(R.color.equalizer_bg)
    }

    private fun playPause(isPlaying: Boolean) {
        playControlStop.visibility(isPlaying)
        playControlPlay.visibility(!isPlaying)

        if (isPlaying) {
            equalizerView.start()
        } else {
            equalizerView.stop()
        }
    }

    override fun onPause() {
        playPause(false)

        super.onPause()
    }
}

fun View.visibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}