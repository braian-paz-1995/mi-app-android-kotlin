package com.atio.log.domain.service

internal interface LogcatProcess {
    suspend fun startLogcat(): Process
}