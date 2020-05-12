package argent.server.features

import argent.util.argentJson
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.serialization.json

object JsonNegotiation: Feature {
    override val installer: Application.() -> Unit = {
        install(ContentNegotiation) { json(argentJson) }
    }
}

object Gzip: Feature {
    override val installer: Application.() -> Unit = {
        install(Compression) { gzip() }
    }
}