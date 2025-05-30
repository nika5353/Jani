package com.eat.jani.models.enums

enum class DietDistribution {
    Default,
    EnergyRiseThroughoutDay,       // Low > Mid > High
    EnergyPeakAfternoon,           // Low > High > Mid
    EnergyPeakNight,               // Mid > Low > High
    EnergyPeakAfternoonLowNight,   // Mid > High > Low
    EnergyPeakMorning              // High > Low > Mid
}