package argent.util

import net.logstash.logback.argument.StructuredArgument
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun e(vararg keyValues: Pair<String,Any?>): StructuredArgument? {
    return StructuredArguments.entries(keyValues.toMap().mapValues { (_, v) -> v.toString() })
}

inline val <reified T> T.logger: Logger
    get() = LoggerFactory.getLogger(T::class.java)
