package com.ationet.androidterminal.core.domain.hal.printer

enum class PrinterStatus {
    Ok,
    Busy,
    OutOfPaper,
    Overheat,
    UnderVoltage,
    Error,
    DriverError,
}