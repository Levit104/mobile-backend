package itmo.plugins

import itmo.ClientImitator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

suspend fun startTest() {
    coroutineScope {
        val startTime = System.currentTimeMillis()
        repeat(10000) { index ->
            launch {
                ClientImitator(index + 1).init()
            }
        }
        joinAll()
        println((System.currentTimeMillis() - startTime) / 1000)
    }
}
