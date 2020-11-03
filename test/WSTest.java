import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
		ws = null;
	}
	
	// ----------------------------------------------------------------------------------------------------

	private WeatherStation ws;

	@Test
	void testGetCurrentTemp0() throws Exception {

		ws.setCurrentTemp(0.0, 3000);
		assertEquals(0.0, ws.getCurrentTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 1.0, 2.0, 3.0, 4.0 })
	void testGetCurrentTemp1(double arg) throws Exception {

		ws.setCurrentTemp(arg, 3000);
		assertEquals(arg, ws.getCurrentTemp());
	}

	@ParameterizedTest
	@ValueSource(doubles = { -Double.MAX_VALUE, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE })
	void testGetCurrentTemp2(double arg) throws Exception {

		double[] dummyValues = { 0.0, 1.0, 2.0, 3.0, 4.0, -70000.8888, 76578765.465747 };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}

		ws.setCurrentTemp(arg, 0);
		assertEquals(arg, ws.getCurrentTemp());

	}

	@Test
	void testGetCurrentTemp_notMeassuredException() {
		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTemp();
		});

	}

	// ----------------------------------------------------------------------------------------------------

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -10000, -1, 0, 1, 2, 3, 4, Long.MAX_VALUE })
	void testGetCurrentTempTime(long arg) throws WeatherStation.WeatherStationException {

		long[] dummyValues = { 0, 1, 2, 3, 4, -70000, 76578765 };
		for (long l : dummyValues) {
			ws.setCurrentTemp(0.0, l);
		}

		ws.setCurrentTemp(0.0, arg);
		assertEquals(arg, ws.getCurrentTempTime());

	}

	@Test
	void testGetCurrentTempTime_notMeassuredException() {
		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTempTime();
		});

	}

	// ----------------------------------------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(doubles = { 0.0, -1.0, -2.0, -3.0, -4.0, -Double.MAX_VALUE })
	void testGetMinTemp0(double arg) throws Exception {

		double[] dummyValues = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}
		ws.setCurrentTemp(arg, 0);
		assertEquals(arg, ws.getMinTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE })
	void testGetMinTemp1(double arg) throws Exception {

		double[] dummyValues = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}
		ws.setCurrentTemp(arg, 0);
		assertEquals(0.0, ws.getMinTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, -1.0, -2.0, -3.0, -4.0, -Double.MAX_VALUE, Double.MAX_VALUE })
	void testGetMinTemp2(double arg) throws Exception {

		ws.setCurrentTemp(-Double.MAX_VALUE, 0);

		ws.setCurrentTemp(arg, 0);
		assertEquals(-Double.MAX_VALUE, ws.getMinTemp());

	}

	@Test
	void testGetMinTemp_notMeassuredException() {
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMinTemp();
		});

	}

	// ----------------------------------------------------------------------------------------------------
	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE })
	void testGetMaxTemp0(double arg) throws Exception {

		double[] dummyValues = { 0.0, -1.0, -2.0, -3.0, -4.0, -Double.MAX_VALUE };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}
		ws.setCurrentTemp(arg, 0);
		assertEquals(arg, ws.getMaxTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, -1.0, -2.0, -3.0, -4.0, -Double.MAX_VALUE })
	void testGetMaxTemp1(double arg) throws Exception {

		double[] dummyValues = { 0.0, -1.0, -2.0, -3.0, -4.0, -Double.MAX_VALUE };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}
		ws.setCurrentTemp(arg, 0);
		assertEquals(0.0, ws.getMaxTemp());

	}

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE, -Double.MAX_VALUE })
	void testGetMaxTemp2(double arg) throws Exception {

		ws.setCurrentTemp(Double.MAX_VALUE, 0);

		ws.setCurrentTemp(arg, 0);
		assertEquals(Double.MAX_VALUE, ws.getMaxTemp());

	}

	@Test
	void testGetMaxTemp_notMeassuredException() {
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMaxTemp();
		});

	}
	// ----------------------------------------------------------------------------------------------------

	@ParameterizedTest
	@ValueSource(doubles = { 0.0, 1.0, 2.0, 3.0, 4.0, Double.MAX_VALUE })
	void testreset0(double arg) throws Exception {

		ws.setCurrentTemp(arg, 0);

		ws.reset();

		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTemp();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTempTime();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMinTemp();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMaxTemp();
		});

		assertEquals(false, ws.getMeasuredOneValid());

	}

	@Test
	void testreset1() throws Exception {
		double[] dummyValues = { 0.0, -1.0, 2.0, -3.0, 4.0, -Double.MAX_VALUE, Double.MAX_VALUE };
		for (double d : dummyValues) {
			ws.setCurrentTemp(d, 0);
		}

		ws.reset();

		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTemp();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getCurrentTempTime();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMinTemp();
		});
		Assertions.assertThrows(Exception.class, () -> {
			ws.getMaxTemp();
		});

		assertEquals(false, ws.getMeasuredOneValid());

	}

}
