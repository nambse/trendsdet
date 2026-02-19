package com.example.trend_sdet.util

import com.shopify.buy3.GraphCall
import com.shopify.buy3.GraphCallResult
import com.shopify.buy3.GraphResponse
import com.shopify.graphql.support.AbstractResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T : AbstractResponse<T>> GraphCall<T>.await(): GraphResponse<T> =
    suspendCancellableCoroutine { continuation ->
        enqueue { result ->
            when (result) {
                is GraphCallResult.Success -> {
                    if (continuation.isActive) {
                        continuation.resume(result.response)
                    }
                }
                is GraphCallResult.Failure -> {
                    if (continuation.isActive) {
                        continuation.resumeWithException(result.error)
                    }
                }
            }
        }

        continuation.invokeOnCancellation { cancel() }
    }
