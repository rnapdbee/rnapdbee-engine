package pl.poznan.put.rnapdbee.engine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.MixedIntegerLinearProgrammingConverter;

@SpringBootTest
class RnaPDBeeEngineApplicationTests {

  @MockBean
  MixedIntegerLinearProgrammingConverter mixedIntegerLinearProgrammingConverter;

  @Test
  void contextLoads() {
  }
}
