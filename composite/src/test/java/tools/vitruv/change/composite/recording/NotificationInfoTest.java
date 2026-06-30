package tools.vitruv.change.composite.recording;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NotificationInfoTest {
  @ParameterizedTest
  @MethodSource("eventTypeDebugNames")
  void debugStringIncludesEventTypeName(final int eventType, final String eventTypeName) {
    final NotificationInfo notificationInfo =
        new NotificationInfo(
            notification(
                eventType, EcorePackage.Literals.ENAMED_ELEMENT__NAME, "before", "after"));

    assertEquals(
        eventTypeName + " val: null / on: 'Owner'.name / old: before / new: after",
        notificationInfo.getDebugString());
  }

  @Test
  void debugStringIncludesReferenceFeatureNameAndEObjectValues() {
    final NotificationInfo notificationInfo =
        new NotificationInfo(
            notification(
                Notification.REMOVE,
                EcorePackage.Literals.ECLASS__ESUPER_TYPES,
                namedClass("Old"),
                namedClass("New")));

    assertEquals(
        "REMOVE val: null / on: 'Owner'.eSuperTypes / old: 'Old' / new: 'New'",
        notificationInfo.getDebugString());
  }

  @Test
  void debugStringLeavesFeatureNameEmptyForNonStructuralFeatureNotifications() {
    final NotificationInfo notificationInfo =
        new NotificationInfo(notification(Notification.MOVE, null, "before", "after"));

    assertEquals(
        "MOVE val: null / on: 'Owner'. / old: before / new: after",
        notificationInfo.getDebugString());
  }

  private static Stream<Arguments> eventTypeDebugNames() {
    return Stream.of(
        Arguments.of(Notification.ADD, "ADD"),
        Arguments.of(Notification.SET, "SET"),
        Arguments.of(Notification.ADD_MANY, "ADDMANY"),
        Arguments.of(Notification.REMOVE, "REMOVE"),
        Arguments.of(Notification.REMOVE_MANY, "REMOVEMANY"),
        Arguments.of(Notification.MOVE, "MOVE"),
        Arguments.of(Notification.UNSET, String.valueOf(Notification.UNSET)));
  }

  private static Notification notification(
      final int eventType,
      final EStructuralFeature feature,
      final Object oldValue,
      final Object newValue) {
    final EObject notifier = namedClass("Owner");
    return new ENotificationImpl(
        ((InternalEObject) notifier), eventType, feature, oldValue, newValue);
  }

  private static EClass namedClass(final String name) {
    final EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    return eClass;
  }
}
