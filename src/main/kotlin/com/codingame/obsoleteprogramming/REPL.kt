package com.codingame.obsoleteprogramming

import java.io.InputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.util.*

internal class REPL(outputStream: OutputStream) {

    private val writer = PrintWriter(outputStream)
    private val parser = Parser()
    private val stack = Stack<Int>()

    fun connectWith(inputStream: InputStream) {

        inputStream.use {

            val scanner = Scanner(inputStream)
            val n = scanner.nextInt()
            if (scanner.hasNextLine()) {
                scanner.nextLine()
            }

            writer.use {

                for (i in 0 until n) {
                    val line = scanner.nextLine()
                    exec(line)
                }

            }

        }

    }

    private fun exec(line: String) {
        val instructions = parser.parse(line)
        instructions.forEach { exec(it) }
    }

    private fun exec(instruction: Instruction) {
        when (instruction) {
            is NumberInstruction -> putNumber(instruction)
            is AddInstruction -> add()
            is SubInstruction -> sub()
            is MulInstruction -> mul()
            is DivInstruction -> div()
            is ModInstruction -> mod()
            is OutInstruction -> println()
            is PopInstruction -> pop()
            is DupInstruction -> dup()
            is SwpInstruction -> swp()
            is RotInstruction -> rot()
            is OvrInstruction -> ovr()
            is PosInstruction -> pos()
            is NotInstruction -> not()
            is FunctionDefinition -> noop()
            is FunctionCall -> callFunction(instruction.referencedFunctionDefinition)
            else -> throw UnexpectedInstructionException(instruction)
        }
    }

    private fun add() = executeTwoParamsOperation(Int::plus)

    private fun sub() = executeTwoParamsOperation(Int::minus)
    private fun mul() = executeTwoParamsOperation(Int::times)
    private fun div() = executeTwoParamsOperation(Int::div)
    private fun mod() = executeTwoParamsOperation(Int::mod)
    private fun executeTwoParamsOperation(operation: (first: Int, second: Int) -> Int) {
        val second = stack.pop()
        val first = stack.pop()
        val result = operation(first, second)
        stack.push(result)
    }

    private fun putNumber(token: NumberInstruction) {
        stack.push(token.value)
    }

    private fun println() {
        val number = stack.pop()
        writer.println(number)
    }

    private fun pop() {
        stack.pop()
    }

    private fun dup() {
        stack.push(stack.peek())
    }

    private fun swp() {
        val first = stack.pop()
        val second = stack.pop()
        stack.push(first)
        stack.push(second)
    }

    private fun rot() {
        val first = stack.pop()
        val second = stack.pop()
        val third = stack.pop()
        stack.push(second)
        stack.push(first)
        stack.push(third)
    }

    private fun ovr() {
        stack.push(stack.secondElement())
    }

    private fun pos() {
        executeOneParamOperation { if (it >= 0) 1 else 0 }
    }

    private fun not() {
        executeOneParamOperation { if (it == 0) 1 else 0 }
    }

    private fun executeOneParamOperation(operation: (param: Int) -> Int) {
        val param = stack.pop()
        val result = operation(param)
        stack.push(result)
    }

    private fun noop() {
        // Nothing to do
    }

    private fun callFunction(referencedFunctionDefinition: FunctionDefinition) {
        referencedFunctionDefinition.instructions.forEach { exec(it) }
    }
}

private fun <E> Stack<E>.secondElement(): E {
    return get(size - 2)
}

class UnexpectedInstructionException(instruction: Instruction) : RuntimeException("Unexpected instruction $instruction")
