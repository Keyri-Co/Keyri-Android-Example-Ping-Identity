package com.keyri.examplepingidentity.network

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import kotlin.coroutines.resumeWithException

class FlowResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (Flow::class.java != getRawType(returnType)) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                "Flow return type must be parameterized as Flow<Foo>"
            )
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawDeferredType = getRawType(responseType)

        return if (rawDeferredType == Result::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                    "Response must be parameterized as Response<Foo> or Response<out Foo>"
                )
            }
            ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {
            BodyCallAdapter<Any>(responseType)
        }
    }
}

internal class ResponseCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<T, Flow<Result<T?>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<Result<T?>> = flow {
        emit(suspendCancellableCoroutine { continuation ->
            call.registerCallback(continuation) { response ->
                continuation.resumeWith(kotlin.runCatching {
                    if (response.isSuccessful) {
                        Result.success(response.body())
                    } else {
                        Result.failure(HttpException(response))
                    }
                })
            }

            call.registerOnCancellation(continuation)
        })
    }
}

internal class BodyCallAdapter<T>(private val responseType: Type) :
    CallAdapter<T, Flow<Result<T>>> {

    override fun responseType(): Type = responseType

    override fun adapt(call: Call<T>): Flow<Result<T>> = flow {
        emit(suspendCancellableCoroutine { continuation ->
            call.registerCallback(continuation) { response ->
                continuation.resumeWith(kotlin.runCatching {
                    if (response.isSuccessful) {
                        response.body()?.let { body -> Result.success(body) } ?: Result.failure(
                            NullPointerException("Response body is null: $response")
                        )
                    } else {
                        Result.failure(HttpException(response))
                    }
                })
            }

            call.registerOnCancellation(continuation)
        })
    }
}

internal fun Call<*>.registerOnCancellation(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCancellation {
        try {
            cancel()
        } catch (e: Exception) {
            // Ignore cancel exception
        }
    }
}

internal fun <T> Call<T>.registerCallback(
    continuation: CancellableContinuation<*>,
    success: (response: Response<T>) -> Unit
) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            success(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            continuation.resumeWithException(t)
        }
    })
}
