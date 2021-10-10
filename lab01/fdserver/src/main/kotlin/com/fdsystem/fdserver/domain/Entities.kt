package com.fdsystem.fdserver.domain

data class ArterialPressure(val topPressure: Int = 0, val botPressure: Int = 0)

data class Pulse(val pulse: Int)

data class Breathe(val rate: Float, val type: String)

data class Cortisol(val cortLevel: Float)

data class MouseClicks(val clicksRate: Float)

data class MouseSpeed(val speed: Float)

data class DHEA(val level: Float)

data class PrintSpeed(val speed: Float)

data class BlinkRate(val rate: Float)

data class FacialExpression(val expression: String)

data class SpeechRate(val rate: Float)