package com.hescul.urgent.ui.versatile.config

import com.hescul.urgent.ui.versatile.LoadingButton

object LoadingButtonConfig {
    /**
     * Decide the time (ms) during which the [LoadingButton] animation
     * transforms the button between 2 states.
     */
    const val STATE_TRANSITION_DURATION = 500

    /**
     * Decide the time (ms) during which the [LoadingButton] animation
     * fades in the button text when it gets transformed to the text state.
     */
    const val TEXT_FADE_IN_DELAY = 60

    /**
     * Decide the font size (in sp) for the text displayed
     * in the [LoadingButton] composable
     */
    const val TEXT_FONT_SIZE = 15

    /**
     * The default width of the [LoadingButton] composable
     */
    const val DEFAULT_WIDTH = 280

    /**
     * The default height of the [LoadingButton] composable
     */
    const val DEFAULT_HEIGHT = 54

    /**
     * The width of the [LoadingButton] composable when it is
     * in progress state
     */
    const val PROGRESS_WIDTH = 80
}