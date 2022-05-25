package com.kkafara.rt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public class Result<OkT, ErrT> {

  public static final String ERR_OK_ACCESS_WHEN_ERR =
      "Attempt to access ok value on error result";

  public static final String ERR_ERR_ACCESS_WHEN_OK =
      "Attempt to access error value on ok result";

  public static final String ERR_DIRECT_ACCESS_WHEN_NULL =
      "Attempt to access null value via safe method. Consider using getOkOrNull / getErrOrNull";

  @Nullable
  private final OkT okValue;

  @Nullable
  private final ErrT errValue;

  @NotNull
  private final Type type;

  private Result(@Nullable OkT okValue, @Nullable ErrT errorValue, @NotNull Type type) {
    this.okValue = okValue;
    this.errValue = errorValue;
    this.type = type;
  }

  public static <S, E> Result<S, E> ok(@Nullable S okValue) {
    return new Result<>(okValue, null, Type.OK);
  }

  public static <S, E> Result<S, E> ok() {
    return new Result<>(null, null, Type.OK);
  }

  public static <S, E> Result<S, E> err(@Nullable E errorValue) {
    return new Result<>(null, errorValue, Type.ERR);
  }

  public static <S, E> Result<S, E> err() {
    return new Result<>(null, null, Type.ERR);
  }

  public boolean isErr() {
    return type == Type.ERR;
  }

  public boolean isOk() {
    return type == Type.OK;
  }

  @NotNull
  public Type getType() {
    return type;
  }

  @Nullable
  public OkT getOkOrNull() {
    if (isOk()) {
      return okValue;
    }
    throw new IllegalStateException(ERR_OK_ACCESS_WHEN_ERR);
  }

  @Nullable
  public ErrT getErrOrNull() {
    if (isErr()) {
      return errValue;
    }
    throw new IllegalStateException(ERR_ERR_ACCESS_WHEN_OK);
  }

  @Contract("!null -> !null; null -> _")
  public OkT getOkOrDefault(@Nullable final OkT value) {
    if (isOk()) {
      return okValue != null ? okValue : value;
    }
    throw new IllegalStateException(ERR_OK_ACCESS_WHEN_ERR);
  }

  @Contract("!null -> !null; null -> _")
  public ErrT getErrOrDefault(@Nullable final ErrT value) {
    if (isErr()) {
      return errValue != null ? errValue : value;
    }
    throw new IllegalStateException(ERR_ERR_ACCESS_WHEN_OK);
  }

  /**
   * If the result is OK and the value is not null, then returns the wrapped nullable value, else it throws.
   *
   * @return wrapped value or throws {@link IllegalStateException}.
   */
  @NotNull
  public OkT getOk() {
    if (isOk() && okValue != null) {
      return okValue;
    }
    throw new IllegalStateException(ERR_OK_ACCESS_WHEN_ERR + " or " + ERR_DIRECT_ACCESS_WHEN_NULL);
  }

  /**
   * If the result is ERR and the value is not null, then returns the wrapped nullable, error value, else it throws.
   *
   * @return wrapped error value or throws {@link IllegalStateException}.
   */
  @NotNull
  public ErrT getErr() {
    if (isErr() && errValue != null) {
      return errValue;
    } else {
      throw new IllegalStateException(ERR_ERR_ACCESS_WHEN_OK + " or " + ERR_DIRECT_ACCESS_WHEN_NULL);
    }
  }

  @NotNull
  public Optional<OkT> getOkOpt() {
    return Optional.ofNullable(getOkOrNull());
  }

  @NotNull
  public Optional<ErrT> getErrOpt() {
    return Optional.ofNullable(getErrOrNull());
  }

  public void ifOkOrElse(Consumer<OkT> okAction, Consumer<ErrT> errAction) {
    if (isOk()) {
      okAction.accept(okValue);
    } else {
      errAction.accept(errValue);
    }
  }

  public void ifOk(Consumer<OkT> action) {
    if (isOk()) {
      action.accept(okValue);
    }
  }

  public void ifErr(Consumer<ErrT> action) {
    if (isErr()) {
      action.accept(errValue);
    }
  }


  public boolean isOkValueNull() {
    return okValue == null;
  }

  public boolean isErrValueNull() {
    return errValue == null;
  }

  public boolean isEmpty() {
    return errValue == null && okValue == null;
  }

  public boolean isPresent() {
    return errValue != null || okValue != null;
  }

  /**
   * Describes the result type.
   */
  public enum Type {
    OK, ERR
  }
}
