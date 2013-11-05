package uk.co.itstherules.marklog.sync;

import org.junit.Test;

public final class FtpCodeFailuresTest {

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindDodgyCode1001() {
        FtpCode.codeFor(1001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindDodgyCode99() {
        FtpCode.codeFor(99);
    }

}
