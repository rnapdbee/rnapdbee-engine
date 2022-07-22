package pl.poznan.put.rnapdbee.engine.calculation.logic;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EncodingUtilsTest {

    @ParameterizedTest
    @CsvFileSource(resources = "/base64cases/base64testcases.csv")
    public void shouldDecodeBase64DecodeProvidedInput(String input, String expected) {
        String actual = EncodingUtils.decodeBase64StringToString(input);
        Assertions.assertEquals(expected, actual);
    }
}