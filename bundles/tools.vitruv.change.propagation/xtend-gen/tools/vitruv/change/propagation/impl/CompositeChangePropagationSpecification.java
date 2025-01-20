package tools.vitruv.change.propagation.impl;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtend.lib.annotations.AccessorType;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Pure;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractor;
import tools.vitruv.change.propagation.ChangePropagationObserver;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.ResourceAccess;

@SuppressWarnings("all")
public class CompositeChangePropagationSpecification extends AbstractChangePropagationSpecification implements ChangePropagationObserver {
  private static final Logger logger = Logger.getLogger(CompositeChangePropagationSpecification.class);

  @Accessors(AccessorType.PROTECTED_GETTER)
  private final List<ChangePropagationSpecification> changePreprocessors;

  @Accessors(AccessorType.PROTECTED_GETTER)
  private final List<ChangePropagationSpecification> changeMainprocessors;

  public CompositeChangePropagationSpecification(final MetamodelDescriptor sourceMetamodel, final MetamodelDescriptor targetMetamodel) {
    super(sourceMetamodel, targetMetamodel);
    ArrayList<ChangePropagationSpecification> _arrayList = new ArrayList<ChangePropagationSpecification>();
    this.changePreprocessors = _arrayList;
    ArrayList<ChangePropagationSpecification> _arrayList_1 = new ArrayList<ChangePropagationSpecification>();
    this.changeMainprocessors = _arrayList_1;
  }

  /**
   * Adds the specified change processor as a preprocessor, which is executed before the mainprocessors.
   * The preprocessors are executed in the order in which they are added.
   */
  protected void addChangePreprocessor(final ChangePropagationSpecification changePropagationSpecifcation) {
    this.assertMetamodelsCompatible(changePropagationSpecifcation);
    this.changePreprocessors.add(changePropagationSpecifcation);
    changePropagationSpecifcation.setUserInteractor(this.getUserInteractor());
    changePropagationSpecifcation.registerObserver(this);
  }

  /**
   * Adds the specified change processor as a main processor, which is executed after the preprocessors.
   * The main processors are executed in the order in which they are added.
   */
  protected void addChangeMainprocessor(final ChangePropagationSpecification changePropagationSpecifcation) {
    this.assertMetamodelsCompatible(changePropagationSpecifcation);
    this.changeMainprocessors.add(changePropagationSpecifcation);
    changePropagationSpecifcation.setUserInteractor(this.getUserInteractor());
    changePropagationSpecifcation.registerObserver(this);
  }

  private void assertMetamodelsCompatible(final ChangePropagationSpecification potentialChangeProcessor) {
    if (((!this.getSourceMetamodelDescriptor().equals(potentialChangeProcessor.getSourceMetamodelDescriptor())) || 
      (!this.getTargetMetamodelDescriptor().equals(potentialChangeProcessor.getTargetMetamodelDescriptor())))) {
      throw new IllegalArgumentException("ChangeProcessor metamodels are not compatible");
    }
  }

  @Override
  public void propagateChange(final EChange<EObject> change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final ResourceAccess resourceAccess) {
    this.propagateChangeViaPreprocessors(change, correspondenceModel, resourceAccess);
    this.propagateChangeViaMainprocessors(change, correspondenceModel, resourceAccess);
  }

  protected void propagateChangeViaPreprocessors(final EChange<EObject> change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final ResourceAccess resourceAccess) {
    for (final ChangePropagationSpecification changeProcessor : this.changePreprocessors) {
      {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Calling change preprocessor ");
        _builder.append(changeProcessor);
        _builder.append(" for change event ");
        _builder.append(change);
        CompositeChangePropagationSpecification.logger.trace(_builder);
        changeProcessor.propagateChange(change, correspondenceModel, resourceAccess);
      }
    }
  }

  protected void propagateChangeViaMainprocessors(final EChange<EObject> change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel, final ResourceAccess resourceAccess) {
    for (final ChangePropagationSpecification changeProcessor : this.changeMainprocessors) {
      {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("Calling change mainprocessor ");
        _builder.append(changeProcessor);
        _builder.append(" for change event ");
        _builder.append(change);
        CompositeChangePropagationSpecification.logger.trace(_builder);
        changeProcessor.propagateChange(change, correspondenceModel, resourceAccess);
      }
    }
  }

  @Override
  public boolean doesHandleChange(final EChange<EObject> change, final EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    ArrayList<ChangePropagationSpecification> _allProcessors = this.getAllProcessors();
    for (final ChangePropagationSpecification changeProcessor : _allProcessors) {
      boolean _doesHandleChange = changeProcessor.doesHandleChange(change, correspondenceModel);
      if (_doesHandleChange) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setUserInteractor(final UserInteractor userInteractor) {
    super.setUserInteractor(userInteractor);
    ArrayList<ChangePropagationSpecification> _allProcessors = this.getAllProcessors();
    for (final ChangePropagationSpecification changeProcessor : _allProcessors) {
      changeProcessor.setUserInteractor(userInteractor);
    }
  }

  private ArrayList<ChangePropagationSpecification> getAllProcessors() {
    final ArrayList<ChangePropagationSpecification> processors = new ArrayList<ChangePropagationSpecification>();
    if ((this.changePreprocessors != null)) {
      Iterables.<ChangePropagationSpecification>addAll(processors, this.changePreprocessors);
    }
    if ((this.changeMainprocessors != null)) {
      Iterables.<ChangePropagationSpecification>addAll(processors, this.changeMainprocessors);
    }
    return processors;
  }

  @Override
  public void objectCreated(final EObject createdObject) {
    this.notifyObjectCreated(createdObject);
  }

  @Pure
  protected List<ChangePropagationSpecification> getChangePreprocessors() {
    return this.changePreprocessors;
  }

  @Pure
  protected List<ChangePropagationSpecification> getChangeMainprocessors() {
    return this.changeMainprocessors;
  }
}
