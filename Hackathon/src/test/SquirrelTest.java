package test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nutty.Squirrel;

/**
 * @author JoelNeppel
 *
 */
class SquirrelTest
{
	@Test
	void testToBytes()
	{
		Squirrel s1 = new Squirrel(0);
		assertEquals(Arrays.toString(s1.getBytes()), "[0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]");
		Squirrel s2 = new Squirrel(102, 56, 78);
		assertEquals(Arrays.toString(s2.getBytes()), "[0, 0, 0, 102, 0, 0, 0, 56, 0, 0, 0, 78, 0, 0, 0, 0, 0, 0, 0, 0]");
		Squirrel s3 = new Squirrel(256, 512, 512, "ABC");
		assertEquals(Arrays.toString(s3.getBytes()), "[0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 3, 65, 66, 67]");
	}
}