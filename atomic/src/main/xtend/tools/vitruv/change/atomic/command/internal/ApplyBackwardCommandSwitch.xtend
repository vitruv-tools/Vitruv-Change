package tools.vitruv.change.atomic.command.internal

import edu.kit.ipd.sdq.activextendannotations.Utility
import java.util.List
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.LogManager
import org.eclipse.emf.common.command.Command
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.edit.command.AddCommand
import org.eclipse.emf.edit.command.RemoveCommand
import org.eclipse.emf.edit.command.SetCommand
import tools.vitruv.change.atomic.EChange
import tools.vitruv.change.atomic.command.RemoveAtCommand
import tools.vitruv.change.atomic.eobject.CreateEObject
import tools.vitruv.change.atomic.eobject.DeleteEObject
import tools.vitruv.change.atomic.feature.UnsetFeature
import tools.vitruv.change.atomic.feature.attribute.InsertEAttributeValue
import tools.vitruv.change.atomic.feature.attribute.RemoveEAttributeValue
import tools.vitruv.change.atomic.feature.attribute.ReplaceSingleValuedEAttribute
import tools.vitruv.change.atomic.feature.reference.InsertEReference
import tools.vitruv.change.atomic.feature.reference.RemoveEReference
import tools.vitruv.change.atomic.feature.reference.ReplaceSingleValuedEReference
import tools.vitruv.change.atomic.root.InsertRootEObject
import tools.vitruv.change.atomic.root.RemoveRootEObject

import static extension tools.vitruv.change.atomic.command.internal.ChangeCommandUtil.alreadyContainsObject
import static extension tools.vitruv.change.atomic.command.internal.ChangeCommandUtil.getEditingDomain

/**
 * Switch to create commands for all EChange classes.
 * The commands applies the EChanges backward.
 */
@Utility
package class ApplyBackwardCommandSwitch {
	static val Logger logger = LogManager.getLogger(ApplyBackwardCommandSwitch)
	
	def package dispatch static List<Command> getCommands(EChange<EObject> change) {
		#[]
	}
	
	/**
	 * Dispatch method to create commands to apply a {@link UnsetFeature} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(UnsetFeature<EObject, ?> change) {
		return #[]
	}
	
	/**
	 * Dispatch method to create commands to apply a {@link InsertEAttributeValue} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(InsertEAttributeValue<EObject, ?> change) {
		val editingDomain = change.affectedElement.editingDomain
		if (change.wasUnset) {
			return #[new SetCommand(editingDomain, change.affectedElement, change.affectedFeature, SetCommand.UNSET_VALUE)]
		} else {
			return #[new RemoveAtCommand(editingDomain, change.affectedElement, change.affectedFeature, change.newValue, change.index)]
		}
	}

	/**
	 * Dispatch method to create commands to apply a {@link RemoveEAttributeValue} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(RemoveEAttributeValue<EObject, ?> change) {
		val editingDomain = change.affectedElement.editingDomain
		return #[new AddCommand(editingDomain, change.affectedElement, change.affectedFeature, change.oldValue,
				change.index)]
	}

	/**
	 * Dispatch method to create commands to apply a {@link ReplaceSingleValuedEAttribute} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(ReplaceSingleValuedEAttribute<EObject, ?> change) {
		val editingDomain = change.affectedElement.editingDomain
		return #[new SetCommand(editingDomain, change.affectedElement, change.affectedFeature, if (change.isWasUnset) SetCommand.UNSET_VALUE else change.oldValue)]
	}

	/**
	 * Dispatch method to create commands to apply a {@link InsertEReference} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(InsertEReference<EObject> change) {
		val editingDomain = change.affectedElement.editingDomain
		if(!change.affectedElement.alreadyContainsObject(change.affectedFeature, change.newValue)) {
			if (change.affectedFeature.EOpposite === null) {
				logger.warn("Tried to remove value " + change.newValue + ", but although there is no opposite feature it was not contained in " + change.affectedElement);
			} 
			return #[];
		}
		if (change.wasUnset) {
			return #[new SetCommand(editingDomain, change.affectedElement, change.affectedFeature, SetCommand.UNSET_VALUE)]
		} else {
			return #[new RemoveAtCommand(editingDomain, change.affectedElement, change.affectedFeature, change.newValue, change.index)]
		}
	}

	/**
	 * Dispatch method to create commands to apply a {@link RemoveEReference} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(RemoveEReference<EObject> change) {
		val editingDomain = change.affectedElement.editingDomain
		if(change.affectedElement.alreadyContainsObject(change.affectedFeature, change.oldValue)) {
			if (change.affectedFeature.EOpposite === null) {
				logger.warn("Tried to add value " + change.oldValue + ", but although there is no opposite feature it was already contained in " + change.affectedElement);
			} 
			return #[];
		}
		return #[new AddCommand(editingDomain, change.affectedElement, change.affectedFeature, change.oldValue, change.index)]
	}
	
	/**
	 * Dispatch method to create commands to apply a {@link ReplaceSingleValuedEReference} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(ReplaceSingleValuedEReference<EObject> change) {
		val editingDomain = change.affectedElement.editingDomain
		if(change.affectedElement.alreadyContainsObject(change.affectedFeature, change.oldValue)) {
			if (change.affectedFeature.EOpposite === null) {
				logger.warn("Tried to add value " + change.oldValue + ", but although there is no opposite feature it was already contained in " + change.affectedElement);
			} 
			return #[];
		}
		return #[new SetCommand(editingDomain, change.affectedElement, change.affectedFeature, if (change.isWasUnset) SetCommand.UNSET_VALUE else change.oldValue)]
	}

	/**
	 * Dispatch method to create commands to apply a {@link InsertRootEObject} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(InsertRootEObject<EObject> change) {
		val editingDomain = change.newValue.editingDomain
		return #[new RemoveCommand(editingDomain, change.resource.contents, change.newValue)]
	}

	/**
	 * Dispatch method to create commands to apply a {@link RemoveRootEObject} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(RemoveRootEObject<EObject> change) {
		val editingDomain = change.oldValue.editingDomain
		// Will be automatically removed from resource because object can only be in one resource.
		return #[new AddCommand(editingDomain, change.resource.getContents, change.oldValue, change.index)]
	}

	/**
	 * Dispatch method to create commands to apply a {@link CreateEObject} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(CreateEObject<EObject> change) {
		return #[]
	}

	/**
	 * Dispatch method to create commands to apply a {@link DeleteEObject} change backward.
	 * @param object The change which commands should be created.
	 */
	def package dispatch static List<Command> getCommands(DeleteEObject<EObject> change) {
		return #[]
	}

}
