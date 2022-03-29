package com.hescul.urgent.core.utils


class MessageProcessor {
    companion object {
        private const val FAIL_CAUSE_DELIMITER = '.'
        private const val FAIL_CAUSE_REPLACE_CHAR = ':'

        @JvmStatic
        fun processFailCause(cause: String): String {
            return cause.replace(FAIL_CAUSE_REPLACE_CHAR, FAIL_CAUSE_DELIMITER).substringBefore(FAIL_CAUSE_DELIMITER).trimEnd()
        }
    }
}