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
        val tokens = parser.parse(line)
        tokens.forEach { token ->
            when(token) {
                is NumberToken -> putNumber(token)
                is AddToken -> add()
                is SubToken -> sub()
                is MulToken -> mul()
                is DivToken -> div()
                is ModToken -> mod()
                is OutToken -> println()
                else -> throw UnexpectedTokenException(token)
            }
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

    private fun putNumber(token: NumberToken) {
        stack.push(token.value)
    }

    private fun println() {
        val number = stack.pop()
        writer.println(number)
    }
}

class UnexpectedTokenException(token: Token) : RuntimeException("Unexpected token $token")
