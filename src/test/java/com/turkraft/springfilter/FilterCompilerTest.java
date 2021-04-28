package com.turkraft.springfilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import com.turkraft.springfilter.compiler.Parser;

class FilterCompilerTest {

  @ParameterizedTest
  @ValueSource(strings = {

      "(((a : (((1:2))) or not not not(b : 2 or c : 5))))",

      "x:1 or y:2 and z:3 or ooo:4 and iii:5", "hello(x) : 1", "bibi(  )", "func('hey\\'s')",

      "x : 1", "(x : 1 or y : 2)",

      "not(not(x : 1))",

      "(x : 1 or (y : 2 and z : 3))",

      "(productSpecification in (261, 262, 263, 264, 265) or category is empty)"

  })
  void compilerTest(String input) throws Exception {

    // this is a generic test method
    // we're just checking that the compiled input is equal to the compilation output of itself
    // this will let us catch tokenizer/parser errors and will make sure that the compiler is
    // deterministic

    String output = Parser.parse(input).generate();

    assertEquals(output, Parser.parse(output).generate());

  }

}
