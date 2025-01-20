package tools.vitruv.testutils.activeannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.macro.Active;
import org.junit.jupiter.params.converter.ArgumentConverter;

/**
 * Creates methods that are shortcuts for the creator methods of the provided {@link #factory}. Additionally
 * creates:
 * 
 * <ul>
 * <li>An {@link ArgumentConverter} called {@code NewEObject} for creating new instances of {@link EObject}s from their
 * {@link EClass}’ {@linkplain EClass#getName name}.
 * <li> An {@link ArgumentConverter} called {@code Classifier} for getting an EClassifier by its
 *  {@linkplain EClassifier#getName name}.
 * </ul>
 */
@Active(ModelCreatorsProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@SuppressWarnings("all")
public @interface ModelCreators {
  /**
   * The factory to copy the create methods of.
   */
  public Class<? extends EFactory> factory();
  /**
   * Whether to make the created shortcuts methods static.
   */
  public boolean staticCreators() default false;
  /**
   * A prefix to strip from the factory’s create methods’ names. “{@code create}” will always be stripped before
   * removing the prefix configured here.
   */
  public String stripPrefix() default "";
  /**
   * A prefix to add to all shortcut methods.
   */
  public String prefix() default "";
  /**
   * Replacements for method names. Every odd element is a name to replace that will be replaced by the following even
   * element. These replacements are performed before adding the prefix according to {@link #prefix}, but after
   * removing the prefixes “{@code create}” and {@link #stripPrefix}.
   */
  public String[] replace() default {};
}
