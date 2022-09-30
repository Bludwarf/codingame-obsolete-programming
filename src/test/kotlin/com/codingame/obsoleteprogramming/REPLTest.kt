package com.codingame.obsoleteprogramming

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path

internal class REPLTest {

    @ParameterizedTest
    @ValueSource(
        strings = [
            "01 - Arithmetic test",
            "02 - Stack Manipulations",
            "03 - Logic",
            "04 - Simple function - square",
            "05 - Function and test",
            "06 - Function calling function and nested if",
            "07 - the Queen of functions",
            "08 - I have no loop and I must iterate",
            "09 - Hello Fibonacci !",
            "10 - Integer square root",
        ]
    )
    fun connectWith(baseName: String) {
        val directory = Path.of("src", "test", "resources", "test-sets")
        val inputFile = directory.resolve("$baseName.input.txt")

        val inputStream = Files.newInputStream(inputFile)
        val outputStream = ByteArrayOutputStream()

        val repl = REPL(outputStream)
        repl.connectWith(inputStream)

        val expectedOutputFile = directory.resolve("$baseName.output.txt")
        val expectedOutput = Files.readString(expectedOutputFile)
        assertThat(outputStream.toString()).isEqualTo(expectedOutput)
    }
}
