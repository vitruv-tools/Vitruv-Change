package tools.vitruv.testutils.activeannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtend.lib.macro.Active;

/**
 * Can be applied to a custom {@link EFactory} implementation to have EObjects’ identifier initialized with
 * {@link EcoreUtil#generateUUID}. When this annotation is added to a factory, it will be modified in a way such that
 * all metaclasses inheriting from the {@code identifierMetaclass} will have their id attribute initialized with the
 * result of invoking {@link EcoreUtil#generateUUID}
 */
@Active(WithGeneratedIdsProcessor.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@SuppressWarnings("all")
public @interface WithGeneratedRandomIds {
  /**
   * The generated Java type of the metaclass that declares the identifier feature. All subclasses of this metaclasses
   * will have their identifier initialized.
   */
  public Class<?> identifierMetaclass();
}
