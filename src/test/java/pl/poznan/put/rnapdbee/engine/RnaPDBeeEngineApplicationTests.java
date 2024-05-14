package pl.poznan.put.rnapdbee.engine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.ExternalConverter;

@SpringBootTest
class RnaPDBeeEngineApplicationTests {

  @MockBean
  ExternalConverter externalConverter;

  @Test
  void contextLoads() {
  }
}
