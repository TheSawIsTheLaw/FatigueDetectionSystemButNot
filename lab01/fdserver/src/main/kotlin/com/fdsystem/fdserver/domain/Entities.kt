package com.fdsystem.fdserver.domain

import java.time.Instant

//data class ArterialPressure(val topPressure: Int = -1, val botPressure: Int = -1)
//
//data class Pulse(val pulse: Int = -1)
//
//data class Breathe(val rate: Float = -1.0f, val type: String = "")
//
//data class Cortisol(val cortLevel: Float = -1.0f)
//
//data class MouseClicks(val clicksRate: Float = -1.0f)
//
//data class MouseSpeed(val speed: Float = -1.0f)
//
//data class DHEA(val level: Float = -1.0f)
//
//data class PrintSpeed(val speed: Float = -1.0f)
//
//data class BlinkRate(val rate: Float = -1.0f)
//
//data class FacialExpression(val expression: String = "")
//
//data class SpeechRate(val rate: Float = -1.0f)
//
//data class Characteristics(
//    val arterialPressure: ArterialPressure = ArterialPressure(),
//    val pulse: Pulse = Pulse(),
//    val breathe: Breathe = Breathe(),
//    val cortisol: Cortisol = Cortisol(),
//    val mouseClicks: MouseClicks = MouseClicks(),
//    val mouseSpeed: MouseSpeed = MouseSpeed(),
//    val dhea: DHEA = DHEA(),
//    val printSpeed: PrintSpeed = PrintSpeed(),
//    val blinkRate: BlinkRate = BlinkRate(),
//    val facialExpression: FacialExpression = FacialExpression(),
//    val speechRate: SpeechRate = SpeechRate()
//)
// Разбить файл
data class MeasurementDTO(val value: String, val time: Instant)

data class MeasurementList(
    val measurementName: String,
    val measurements: List<MeasurementDTO>
)

data class MeasurementListLight(
    val measurementName: String,
    val measurements: List<String>
)

data class UserCredentials(
    val username: String, val password: String, val dbToken: String
)

data class UserCredentialsToAuth(
    val username: String, val password: String
)

data class PasswordChangeEntity(
    val oldPassword: String,
    val newPassword: String
)