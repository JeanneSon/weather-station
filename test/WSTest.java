import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WSTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		ws = new WeatherStation();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	WeatherStation ws;

	@Test
	void testGetCurrentTemp0() {
		ws.setCurrentTemp(0.0, 3000);
		assertEquals(0.0, ws.getCurrentTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 1.0, 2.0, 3.0, 4.0 })
	void testGetCurrentTemp(double arg) {

		ws.setCurrentTemp(arg, 3000);
		assertEquals(arg, ws.getCurrentTemp());
	}

	@ParameterizedTest
	@ValueSource(doubles = { 1.0, 2.0, 3.0 })
	void testGetMinTemp(double arg) {

		ws.setCurrentTemp(arg, 3000);
		assertEquals(arg, ws.getMinTemp());
	}

	@ParameterizedTest
	@ValueSource(doubles = { 1.0, 2.0, 3.0 })
	void testGetMaxTemp(double arg) {

		ws.setCurrentTemp(arg, 3000);
		assertEquals(arg, ws.getMaxTemp());
	}

	@Test
	void testMinMaxInfo() {

		ws.setCurrentTemp(1.0, 3000);
		ws.setCurrentTemp(2.0, 3000);
		assertEquals("Minimum: 1.0  Maximum: 2.0", ws.minMaxInfo());
	}
}
