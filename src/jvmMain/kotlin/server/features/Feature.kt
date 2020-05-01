package argent.server.features

import io.ktor.application.Application

interface Feature {
    val installer: Application.() -> Unit
}


fun Application.features(vararg features: Feature){
    features.forEach{
        it.installer(this)
    }
}