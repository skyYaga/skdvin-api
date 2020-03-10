package in.skdv.skdvinbackend;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"skdvin.cors.enabled=true"})
public class SkdvinBackendApplicationTestsCors extends AbstractSkdvinTest {

	@Test
	public void contextLoadsWithCorsEnabled() {

		Assert.assertTrue(true);
	}

}
