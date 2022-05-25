package com.kkafara.rt;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Allows for meaningful representation of the result of any operation. <br>
 * Result can be either of {@link Type}.OK or {@link Type}.ERR type.
 *
 * This class exposes numerous convenience instance methods.
 *
 * See <a href="https://github.com/kkafar/result-type">source code</a>.
 *
 * @param <OkT> success value type
 * @param <ErrT> error value type
 */
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

  /**
   * Constructs Ok result with given value.
   *
   * @param okValue result value
   * @param <S> success type
   * @param <E> error type
   * @return Ok result with provided value
   */
  public static <S, E> Result<S, E> ok(@Nullable S okValue) {
    return new Result<>(okValue, null, Type.OK);
  }

  /**
   * Constructs Ok result with no value inside.
   * @param <S> success type
   * @param <E> error type
   * @return Ok result with no value
   */
  public static <S, E> Result<S, E> ok() {
    return new Result<>(null, null, Type.OK);
  }

  /**
   * Constructs Err result with given value.
   *
   * @param errorValue result value
   * @param <S> success type
   * @param <E> error type
   * @return Err result with provided value
   */
  public static <S, E> Result<S, E> err(@Nullable E errorValue) {
    return new Result<>(null, errorValue, Type.ERR);
  }

  /**
   * Constructs Err result with no value inside.
   *
   * @param <S> success type
   * @param <E> error type
   * @return Err result with no value
   */
  public static <S, E> Result<S, E> err() {
    return new Result<>(null, null, Type.ERR);
  }

  /**
   * Checks whether {@link Result} instance is of type {@link Type}.ERR.
   *
   * @return true iff the result is of error type
   */
  public boolean isErr() {
    return type == Type.ERR;
  }

  /**
   * Checks whether {@link Result} instance is of type {@link Type}.OK.
   *
   * @return true iff the result is of ok type
   */
  public boolean isOk() {
    return type == Type.OK;
  }

  /**
   * @return {@link Type}.OK iff the result is success, {@link Type}.ERR otherwise
   */
  @NotNull
  public Type getType() {
    return type;
  }

  /**
   * @return wrapped okValue when the result is of type {@link Type}.OK,
   * throws {@link IllegalStateException} otherwise; null is returned iff the wrapped value is null
   */
  @Nullable
  public OkT getOkOrNull() {
    if (isOk()) {
      return okValue;
    }
    throw new IllegalStateException(ERR_OK_ACCESS_WHEN_ERR);
  }

  /**
   * @return wrapped errorValue when the result is of type {@link Type}.ERR,
   * throws {@link IllegalStateException} otherwise; null is returned iff the wrapped value is null
   */
  @Nullable
  public ErrT getErrOrNull() {
    if (isErr()) {
      return errValue;
    }
    throw new IllegalStateException(ERR_ERR_ACCESS_WHEN_OK);
  }

  /**
   * @param value value to be returned in case of absence of wrapped value
   * @return okValue when it is not null, provided value otherwise; throws {@link IllegalStateException}
   * when the result is not of {@link Type}.OK type
   */
  @Contract("!null -> !null; null -> _")
  public OkT getOkOrDefault(@Nullable final OkT value) {
    if (isOk()) {
      return okValue != null ? okValue : value;
    }
    throw new IllegalStateException(ERR_OK_ACCESS_WHEN_ERR);
  }

  /**
   * @param value value to be returned in case of absence of wrapped value
   * @return errorValue when it is not null, provided value otherwise; throws {@link IllegalStateException}
   * when the result is not of {@link Type}.ERR type
   */
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

  /**
   * @return {@link Optional} with wrapped okValue if the result is of OK type, else it throws
   * {@link IllegalStateException}
   */
  @NotNull
  public Optional<OkT> getOkOpt() {
    return Optional.ofNullable(getOkOrNull());
  }

  /**
   * @return {@link Optional} with wrapped errorValue if the result is of ERR type, else it throws
   * {@link IllegalStateException}
   */
  @NotNull
  public Optional<ErrT> getErrOpt() {
    return Optional.ofNullable(getErrOrNull());
  }

  /**
   * Executes one of the {@link Consumer} actions depending on the result type.
   *
   * @param okAction callback to be executed when the result is OK
   * @param errAction callback to be executed when the result is ERR
   */
  public void ifOkOrElse(Consumer<OkT> okAction, Consumer<ErrT> errAction) {
    if (isOk()) {
      okAction.accept(okValue);
    } else {
      errAction.accept(errValue);
    }
  }

  /**
   * Executes {@link Consumer} action provided that the result type is OK
   * @param action callback to be executed when the result is OK; noop otherwise
   */
  public void ifOk(Consumer<OkT> action) {
    if (isOk()) {
      action.accept(okValue);
    }
  }

  /**
   * Executes {@link Consumer} action provided that the result type is ERR
   * @param action callback to be executed when the result is ERR; noop otherwise
   */
  public void ifErr(Consumer<ErrT> action) {
    if (isErr()) {
      action.accept(errValue);
    }
  }

  /**
   * Checks whether the okValue is null.
   *
   * @return true iff okValue == null
   */
  public boolean isOkValueNull() {
    return okValue == null;
  }

  /**
   * Checks whether the errorValue is null.
   *
   * @return true iff errValue == null;
   */
  public boolean isErrValueNull() {
    return errValue == null;
  }

  /**
   * Checks whether there is no non-null value associated with the result. This method does not
   * take the result type into considerations.
   *
   * @return true iff there is no value wrapped inside the result
   */
  public boolean isEmpty() {
    return errValue == null && okValue == null;
  }

  /**
   * Checks whether there is a non-null value associated with the result. This method does not
   * take the result type into considerations.
   *
   * @return true iff there is a value wrapped inside the result
   */
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
