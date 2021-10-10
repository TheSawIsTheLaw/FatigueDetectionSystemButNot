package com.fdsystem.fdserver.domain

data class ArterialPressure(val topPressure: Int = -1, val botPressure: Int = -1)

data class Pulse(val pulse: Int = -1)

data class Breathe(val rate: Float = -1.0f, val type: String = "")

data class Cortisol(val cortLevel: Float = -1.0f)

data class MouseClicks(val clicksRate: Float = -1.0f)

data class MouseSpeed(val speed: Float = -1.0f)

data class DHEA(val level: Float = -1.0f)

data class PrintSpeed(val speed: Float = -1.0f)

data class BlinkRate(val rate: Float = -1.0f)

data class FacialExpression(val expression: String = "")

data class SpeechRate(val rate: Float = -1.0f)