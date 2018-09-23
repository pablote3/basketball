package com.rossotti.basketball.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.rossotti.basketball.config.SpringConfig.class)
public class PropertySourceTest {
	@Autowired
    Environment env;

	@Test
	public void getProperty_String_Valid() {
		Assert.assertEquals("validString", env.getProperty("accumulator.string.valid", String.class));
	}
	@Test
	public void getProperty_String_Empty() {
        Assert.assertEquals("", env.getProperty("accumulator.string.empty", String.class));
	}
	@Test
	public void getProperty_String_Null() {
        Assert.assertNull(env.getProperty("accumulator.string.null", String.class));
	}

    @Test
    public void getRequiredProperty_String_Valid() {
        Assert.assertEquals("validString", env.getRequiredProperty("accumulator.string.valid", String.class));
    }
    @Test
    public void getRequiredProperty_String_Empty() {
        Assert.assertEquals("", env.getRequiredProperty("accumulator.string.empty"));
    }
    @Test(expected=IllegalStateException.class)
    public void getRequiredProperty_String_Null() {
        Assert.assertNull(env.getRequiredProperty("accumulator.string.null"));
    }

    @Test
    public void getProperty_Int_Valid() {
        Assert.assertEquals(Integer.valueOf(0), env.getProperty("accumulator.int.valid", Integer.class));
    }
	@Test
	public void getProperty_Int_Empty() {
        Assert.assertEquals(null, env.getProperty("accumulator.int.empty", Integer.class));
	}
	@Test(expected= ConversionFailedException.class)
	public void getProperty_Int_NumberFormatException() {
		env.getProperty("accumulator.int.invalid", Integer.class);
	}
}