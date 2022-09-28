package com.codingame.obsoleteprogramming

import java.io.StringReader
import java.text.ParseException
import java.util.*

class Parser {

    private val numberParser = NumberParser()
    private val functionDefinitions = mutableMapOf<String, FunctionDefinition>()

    fun parse(line: String): List<Instruction> {
        val scanner = Scanner(StringReader(line))

        return sequence {
            while (scanner.hasNext()) {
                val instruction = scanner.next()
                if (numberParser.isANumber(instruction)) {
                    yield(numberParser.parse(instruction))
                } else {
                    yield(parseInstruction(instruction, scanner))
                }
            }
        }.toList()
    }

    private fun parseInstruction(
        instruction: String?,
        scanner: Scanner
    ) = when (instruction) {
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
            val referencedFunctionDefinition = functionDefinitions[instruction]
            if (referencedFunctionDefinition != null) {
                FunctionCall(referencedFunctionDefinition)
            } else {
                throw ParseException(instruction, 0)
            }
        }
    }

    private fun parseFunctionDefinition(scanner: Scanner): FunctionDefinition {
        val name = scanner.next()
        val instructions = sequence {
            while (true) {
                val token = scanner.next()
                if (token == "END") break
                val instruction = parseInstruction(token, scanner)
                yield(instruction)
            }
        }.toList()

        val functionDefinition = FunctionDefinition(name, instructions)
        functionDefinitions[functionDefinition.name] = functionDefinition
        return functionDefinition
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
class FunctionCall(val referencedFunctionDefinition: FunctionDefinition) : Instruction()
