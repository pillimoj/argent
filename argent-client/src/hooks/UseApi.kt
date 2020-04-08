import kotlinext.js.asJsObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import react.RReducer
import react.useEffect
import react.useReducer

enum class ActionType {
    REQUEST,
    SUCCESS,
    ERROR
}

sealed class Action<T>(val type: ActionType)
data class REQUEST<T>(val x: Unit = Unit) : Action<T>(ActionType.REQUEST)
data class SUCCESS<T>(val payload: T) : Action<T>(ActionType.SUCCESS)
data class ERROR<T>(val error: String?) : Action<T>(ActionType.ERROR)

sealed class FetchOnMount
data class DoFetchOnMount(val params: Map<String, dynamic>? = null) : FetchOnMount()
object DoNotFetchOnMount : FetchOnMount()

data class ApiUsage<T>(
    val state: State<T>,
    val fetch: suspend () -> Unit
)

data class ApiUsageQuery<T>(
    val state: State<T>,
    val fetch: suspend (Map<String, dynamic>?) -> Unit
){
    fun toApiUsage(): ApiUsage<T> = ApiUsage(state) {fetch(null)}
}

sealed class State<T>(val data: T?)
class Requesting<T>(data: T?) : State<T>(data)
class Success<T>(data: T) : State<T>(data) { val loadedData: T get() = data!! }
class Failed<T>(data: T?, val error: String?) : State<T>(data)

fun <T> createAsyncReducer(): RReducer<State<T>, Action<T>> = { state: State<T>, action: Action<T> ->
    when (action) {
        is REQUEST -> Requesting(state.data)
        is SUCCESS -> Success(action.payload)
        is ERROR -> Failed(state.data, action.error)
    }
}

fun buildParamsString(params: Map<String, dynamic>?): String? = params
    ?.map { (key, value) -> "$key=$value" }
    ?.joinToString(prefix = "?", separator = "&")

fun <T> useApi(
    initialData: T? = null,
    fetchOnMount: FetchOnMount = DoFetchOnMount(),
    asyncFn: suspend () -> T
) = useApiQuery(initialData, fetchOnMount, {_ -> asyncFn()}).toApiUsage()



fun <T> useApiQuery(
    initialData: T?,
    fetchOnMount: FetchOnMount,
    asyncFn: suspend (paramsStr: String?) -> T
): ApiUsageQuery<T> {
    val reducer = createAsyncReducer<T>()
    val (state, dispatch) = useReducer(reducer, Requesting(initialData))

    val usage = ApiUsageQuery(state) { params: Map<String, dynamic>? ->
        try {
            dispatch(REQUEST())
            val response = asyncFn(buildParamsString(params))
            dispatch(SUCCESS(response))
        } catch (e: Throwable) {
            console.error(e.asJsObject())
            dispatch(ERROR("$e"))
        }
    }

    useEffect(emptyList()) {
        if (fetchOnMount is DoFetchOnMount) {
            CoroutineScope(Dispatchers.Default).launch {
                usage.fetch(fetchOnMount.params)
            }
        }
    }

    return usage
}