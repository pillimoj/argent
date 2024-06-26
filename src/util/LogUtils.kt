package argent.util

import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun extra(vararg keyValues: Pair<String, Any?>): StructuredArgument? {
    return StructuredArguments.entries(keyValues.toMap().mapValues { (_, v) -> v.toString() })
}

interface WithLogger
inline val <reified T : WithLogger> T.logger: Logger
    get() = LoggerFactory.getLogger(T::class.java)

fun namedLogger(name: String): Logger = LoggerFactory.getLogger(name)
