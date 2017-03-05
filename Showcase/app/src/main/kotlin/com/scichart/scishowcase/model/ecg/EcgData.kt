package com.scichart.scishowcase.model.ecg

data class EcgData(val xValue: Double, val ecgHeartRate: Double, val bloodPressure: Double, val bloodVolume: Double, val bloodOxygenation: Double, val currentTrace: TraceAOrB)