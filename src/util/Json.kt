package argent.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

val defaultJackson: ObjectMapper.() -> Unit = {
    registerModules(
        KotlinModule(),
        JavaTimeModule()
    )
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
}

val defaultObjectMapper = ObjectMapper().apply(defaultJackson)
