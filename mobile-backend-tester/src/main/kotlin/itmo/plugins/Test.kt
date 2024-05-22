package itmo.plugins

import itmo.ClientImitator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

suspend fun startTest() {
    coroutineScope {
        val startTime = System.currentTimeMillis()
        val job = launch {
            repeat(10000) { index ->
                launch {
                    ClientImitator(index + 1).init()
                }
            }
        }
        job.join()
        println((System.currentTimeMillis() - startTime)/1000)
    }
}
