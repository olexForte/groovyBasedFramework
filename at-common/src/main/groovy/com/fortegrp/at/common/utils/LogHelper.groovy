package com.fortegrp.at.common.utils

import com.fortegrp.at.common.env.Environment
import groovy.util.logging.Slf4j

/**
 * Created by ybelaziorava on 2/6/2015.
 */
@Slf4j
class LogHelper {
    static void logInfo(message){
        log.info("[Fork #$Environment.forkNumber] - $message")
    }

    static void logError(message, object = null){
        log.error("[Fork #$Environment.forkNumber] - $message", object)
    }
}
