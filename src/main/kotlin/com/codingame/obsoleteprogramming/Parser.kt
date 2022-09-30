package com.codingame.obsoleteprogramming

import java.io.StringReader
import java.text.ParseException
import java.util.*

class Parser {

    private val numberParser = NumberParser()
    private val internalFunctionDefinitions = mutableMapOf<String, FunctionDefinition>()
    val functionDefinitions get() = internalFunctionDefinitions.toMap()

    fun parse(code: String): List<Instruction> {
        val scanner = Scanner(StringReader(code))

        return sequence {
            while (scanner.hasNext()) {
                val instruction = scanner.next()
                yield(parseInstruction(instruction, scanner))
            }
        }.toList()
    }

    private fun parseInstruction(
        token: String,
        scanner: Scanner
    ) = if (numberParser.isANumber(token)) {
        numberParser.parse(token)
    } else when (token) {
        "ADD" -> AddInstruction()
        "SUB" -> SubInstruction()
        "MUL" -> MulInstruction()
        "DIV" -> DivInstruction()
        "MOD" -> ModInstruction()
        "OUT" -> OutInstruction()
        "POP" -> PopInstruction()
        "DUP" -> DupInstruction()
        "SWP" -> SwpInstruction()
        "ROT" -> RotInstruction()
        "OVR" -> OvrInstruction()
        "POS" -> PosInstruction()
        "NOT" -> NotInstruction()
        "DEF" -> parseFunctionDefinition(scanner)
        else -> {
            val referencedFunctionDefinition = internalFunctionDefinitions[token]
            if (referencedFunctionDefinition != null) {
                FunctionCall(token)
            } else {
                throw ParseException(token, 0)
            }
        }
    }

    private fun parseFunctionDefinition(scanner: Scanner): FunctionDefinition {
        val name = scanner.next()
        val instructions = sequence {
            while (true) {
                val token = scanner.next()
                if (token == "END") break
                val instruction = parseInstructionInFunctionDefinition(name, token, scanner)
                yield(instruction)
            }
        }.toList()

        val functionDefinition = FunctionDefinition(name, instructions)
        internalFunctionDefinitions[functionDefinition.name] = functionDefinition
        return functionDefinition
    }

    private fun parseInstructionInFunctionDefinition(functionName: String, token: String, scanner: Scanner): Instruction {
        return if (token == "IF") {
            val thenInstructions = mutableListOf<Instruction>()
            val elseInstructions = mutableListOf<Instruction>()
            var instructions = thenInstructions
            while (true) {
                val tokenInConditional = scanner.next()
                if (tokenInConditional == "ELS") {
                    instructions = elseInstructions
                    continue
                }
                if (tokenInConditional == "FI") break
                val instruction = parseInstructionInFunctionDefinition(functionName, tokenInConditional, scanner)
                instructions += instruction
            }
            Conditional(thenInstructions, elseInstructions)
        } else if (token == functionName) {
            FunctionCall(functionName)
        } else {
            parseInstruction(token, scanner)
        }
    }
}

private abstract class InstructionParser<T : Instruction> {
    abstract fun parse(instruction: String): T
}

private class NumberParser : InstructionParser<NumberInstruction>() {
    val regex = Regex("^-?\\d+$")

    override fun parse(instruction: String): NumberInstruction {
        return NumberInstruction(Integer.parseInt(instruction))
    }

    fun isANumber(instruction: String): Boolean {
        return instruction.matches(regex)
    }
}

abstract class Instruction

class NumberInstruction(val value: Int) : Instruction()
class AddInstruction : Instruction()
class SubInstruction : Instruction()
class MulInstruction : Instruction()
class DivInstruction : Instruction()
class ModInstruction : Instruction()
class OutInstruction : Instruction()
class PopInstruction : Instruction()
class DupInstruction : Instruction()

class SwpInstruction() : Instruction()
class RotInstruction() : Instruction()
class OvrInstruction() : Instruction()
class PosInstruction() : Instruction()
class NotInstruction() : Instruction()

class FunctionDefinition(val name: String, val instructions: List<Instruction>) : Instruction()
class FunctionCall(val functionName: String) : Instruction()
class Conditional(val thenInstructions: List<Instruction>, val elseInstructions: List<Instruction>) : Instruction()
