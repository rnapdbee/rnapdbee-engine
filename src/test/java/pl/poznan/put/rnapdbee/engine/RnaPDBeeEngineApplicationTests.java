package pl.poznan.put.rnapdbee.engine;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.poznan.put.rnapdbee.engine.shared.converter.boundary.ExternalConverter;

@SpringBootTest
class RnaPDBeeEngineApplicationTests {

  @MockitoBean
  ExternalConverter externalConverter;

  @Test
  void contextLoads() {
  }
}
