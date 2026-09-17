package com.dirzaaulia.loottrack.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

actual fun createHttpEngine(): HttpClientEngine = Js.create()
