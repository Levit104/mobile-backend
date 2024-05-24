package itmo.plugins

import itmo.ClientImitator
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun startTest() {
    coroutineScope {
        val startTime = System.currentTimeMillis()
        async {
            repeat(1000) { index ->
                launch {
                    ClientImitator(index + 1).init()
                }
            }
        }.await()
        println((System.currentTimeMillis() - startTime) / 1000.0)
    }
}
