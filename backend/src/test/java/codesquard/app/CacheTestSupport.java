package codesquard.app;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "app.scheduling.enable=true")
@SpringBootTest
public abstract class CacheTestSupport {
}
