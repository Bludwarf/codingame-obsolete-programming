package com.codingame.obsoleteprogramming

import java.text.ParseException

class Parser {

    private val separator = Regex(" +")
    private val numberParser = NumberParser()

    fun parse(line: String): List<Token> {
        return line.split(separator).map { instruction ->
            if (numberParser.isANumber(instruction)) {
                numberParser.parse(instruction)
            } else {
                when (instruction) {
                    "ADD" -> AddToken()
                    "SUB" -> SubToken()
                    "MUL" -> MulToken()
                    "DIV" -> DivToken()
                    "MOD" -> ModToken()
                    "OUT" -> OutToken()
                    "POP" -> PopToken()
                    "DUP" -> DupToken()
                    "SWP" -> SwpToken()
                    "ROT" -> RotToken()
                    "OVR" -> OvrToken()
                    "POS" -> PosToken()
                    "NOT" -> NotToken()
                    else -> throw ParseException(instruction, 0)
                }

            }
        }
    }
}

private abstract class InstructionParser<T : Token> {
    abstract fun parse(instruction: String): T
}

private class NumberParser : InstructionParser<NumberToken>() {
    val regex = Regex("^-?\\d+$")

    override fun parse(instruction: String): NumberToken {
        return NumberToken(Integer.parseInt(instruction))
    }

    fun isANumber(instruction: String): Boolean {
        return instruction.matches(regex)
    }
}

abstract class Token

class NumberToken(val value: Int) : Token()
class AddToken : Token()
class SubToken : Token()
class MulToken : Token()
class DivToken : Token()
class ModToken : Token()
class OutToken : Token()
class PopToken : Token()
class DupToken : Token()

class SwpToken() : Token()
class RotToken() : Token()
class OvrToken() : Token()
class PosToken() : Token()
class NotToken() : Token()
