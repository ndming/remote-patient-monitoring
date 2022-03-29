package com.hescul.urgent.core.utils

class InfoValidator {
    companion object {
        private const val EMAIL_REGEX_PATTERN = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[{3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])"
        private const val PASSWORD_MIN_LENGTH = 8

        private const val CONFIRM_CODE_REGEX_PATTERN = "[0-9]+"

        @JvmStatic
        fun isEmailValid(email: String): Boolean {
            return EMAIL_REGEX_PATTERN.toRegex().matches(email)
        }
        @JvmStatic
        fun isPasswordValid(password: String): Boolean {
            return pwMeetsLengthReq(password)
                    && pwMeetsSpecialCharReq(password)
                    && pwMeetsNumberReq(password)
                    && pwMeetsUpperCaseReq(password)
                    && pwMeetsLowerCaseReq(password)
        }
        @JvmStatic
        fun pwMeetsLengthReq(password: String): Boolean {
            return password.length >= PASSWORD_MIN_LENGTH
        }
        @JvmStatic
        fun pwMeetsNumberReq(password: String): Boolean {
            return password.contains("[0-9]".toRegex())
        }
        @JvmStatic
        fun pwMeetsSpecialCharReq(password: String): Boolean {
            return !password.matches("^[a-zA-Z0-9]*\$".toRegex())
        }
        @JvmStatic
        fun pwMeetsUpperCaseReq(password: String): Boolean {
            return password.contains("[A-Z]".toRegex())
        }
        @JvmStatic
        fun pwMeetsLowerCaseReq(password: String): Boolean {
            return password.contains("[a-z]".toRegex())
        }

        @JvmStatic
        fun isConfirmCodeValid(code: String): Boolean {
            return CONFIRM_CODE_REGEX_PATTERN.toRegex().matches(code)
        }
    }
}