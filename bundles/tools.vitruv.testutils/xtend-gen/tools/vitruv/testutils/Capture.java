package tools.vitruv.testutils;

import com.google.common.base.Preconditions;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Helper to capture values that are created in Lambdas, so that they can be used outside the Lambda. This is
 * sometimes necessary because Java never allows writing to variables outside of a Lambda’s scope.
 * <p>
 * Example usage:
 * <pre>
 * <code>
 * def initRepository(String repositoryName) {
 *     val repository = new Capture&lt;Repository&gt;
 *     resourceAt(repositoryModelFor(repositoryName)).propagate [
 * 	       contents += pcm.repository.Repository => [
 * 		       entityName = repositoryName
 * 	       ] >> repository
 *     ]
 *     return +repository
 * }
 * </code>
 * </pre>
 */
@SuppressWarnings("all")
public class Capture<T extends Object> {
  @Accessors(AccessorType.PUBLIC_GETTER)
  private boolean isSet = false;

  private T instance = null;

  /**
   * Sets the current value, overriding a previous value.
   */
  public boolean set(final T value) {
    boolean _xblockexpression = false;
    {
      this.instance = value;
      _xblockexpression = this.isSet = true;
    }
    return _xblockexpression;
  }

  /**
   * Syntactic sugar for {@linkplain #set setting} the {@code value} of this capture.
   * @return {@code value}
   */
  public T operator_add(final T value) {
    this.set(value);
    return value;
  }

  /**
   * Syntactic sugar for {@linkplain #get getting} this capture’s current value.
   */
  public T operator_minus() {
    return this.get();
  }

  /**
   * Gets the current value.
   * 
   * @throws java.lang.IllegalStateException if no value has been set yet.
   */
  public T get() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("No value has been set yet!");
    Preconditions.checkState(this.isSet, _builder);
    return this.instance;
  }

  /**
   * Syntactic sugar for {@linkplain #set setting} the {@code value} of this capture.
   * @return {@code value}
   */
  public static <T extends Object> T operator_doubleGreaterThan(final T value, final Capture<T> capture) {
    capture.operator_add(value);
    return value;
  }

  @Pure
  public boolean getIsSet() {
    return this.isSet;
  }
}
