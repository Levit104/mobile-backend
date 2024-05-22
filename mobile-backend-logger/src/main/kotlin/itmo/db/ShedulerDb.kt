package itmo.db

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

fun schedulerInit(db: LogDataBase) {
    val scheduler = Executors.newSingleThreadScheduledExecutor()

    val task = Runnable {
        println("flush")
        db.flush()
    }

    scheduler.scheduleAtFixedRate(task, 60, 120, TimeUnit.SECONDS)
}