package com.kkafara.rt;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResultTest {
  @Test
  public void throwsOnDirectAccessToNullOkValue() {
    Result<String, Void> result = Result.ok();
    assertThrows(IllegalStateException.class, result::getOk);
  }

  @Test
  public void throwsOnDirectAccessToNullErrValue() {
    Result<Void, String> result = Result.err();
    assertThrows(IllegalStateException.class, result::getErr);
  }

  @Test
  public void throwsOnAccessToErrValueWhenOk() {
    Result<Void, String> result = Result.err("error");
    assertThrows(IllegalStateException.class, result::getOk);
    assertThrows(IllegalStateException.class, result::getOkOrNull);
    assertThrows(IllegalStateException.class, result::getOkOpt);
    assertThrows(IllegalStateException.class, () -> {
      result.getOkOrDefault(null);
    });
  }

  @Test
  public void throwsOnAccessToOkValueWhenErr() {
    Result<String, Void> result = Result.ok("ok");
    assertThrows(IllegalStateException.class, result::getErr);
    assertThrows(IllegalStateException.class, result::getErrOrNull);
    assertThrows(IllegalStateException.class, result::getErrOpt);
    assertThrows(IllegalStateException.class, () -> {
      result.getErrOrDefault(null);
    });
  }

  @Test
  public void returnsCorrectType() {
    Result<Void, Void> resultOk = Result.ok();
    Result<Void, Void> resultErr = Result.err();

    assertEquals(Result.Type.OK, resultOk.getType());
    assertEquals(Result.Type.ERR, resultErr.getType());
  }
}
