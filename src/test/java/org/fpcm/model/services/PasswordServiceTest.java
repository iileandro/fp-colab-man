package org.fpcm.model.services;

import org.fpcm.model.enums.PasswordComplexityEnum;
import org.fpcm.util.passwordmeter.PasswordCheckResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.fpcm.model.enums.PasswordComplexityEnum.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("h2db")
public class PasswordServiceTest {

	@Autowired
	PasswordService passwordService;

	@Test
	public void testCase0() {
		assertCase("22222222", 0,  VERY_WEAK);
	}

	@Test
	public void testCase4() {
		assertCase("9876543210", 4,  VERY_WEAK);
	}

	@Test
	public void testCase11() {
		assertCase("asdadasdjkadads", 11,  VERY_WEAK);
	}

	@Test
	public void testCase27() {
		assertCase("29323943423492234249234452452", 27,  WEAK);
	}

	@Test
	public void testCase32() {
		assertCase("sdd999999999", 32,  WEAK);
	}

	@Test
	public void testCase39() {
		assertCase("0di0jasdas", 39,  WEAK);
	}

	@Test
	public void testCase42() {
		assertCase("ABC%$#", 42,  GOOD);
	}

	@Test
	public void testCase54() {
		assertCase("9%¨&*", 54,  GOOD);
	}

	@Test
	public void testCase64() {
		assertCase("&*()123", 64,  STRONG);
	}

	@Test
	public void testCase65() {
		assertCase("123Mudar", 65,  STRONG);
	}

	@Test
	public void testCase77() {
		assertCase("88888sss123", 77,  STRONG);
	}

	@Test
	public void testCase79() {
		assertCase(")&¨&%#$", 79,  STRONG);
	}

	@Test
	public void testCase83() {
		assertCase("123#Mudar", 83,  VERY_STRONG);
	}

	@Test
	public void testCase97() {
		assertCase("123#Mudar!", 97,  VERY_STRONG);
	}

	@Test
	public void testCase100() {;
		assertCase("Xdsfo934534lkds$%#", 100,  VERY_STRONG);
		assertCase("s=99wewq9edasj9as9dasd3939eqwejAS(%#", 100,  VERY_STRONG);
	}

	private void assertCase(String typedTxt, int expectedScore, PasswordComplexityEnum expectedComplexity) {
		PasswordCheckResult result = passwordService.check(typedTxt);
		assertEquals(expectedScore, result.getScore());
		assertEquals(expectedComplexity, result.getComplexity());
	}

}
