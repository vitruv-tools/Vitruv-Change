package tools.vitruv.change.interaction.impl;

import java.util.Random;
import org.apache.log4j.Logger;
import org.eclipse.xtend.lib.annotations.Delegate;
import org.eclipse.xtext.xbase.lib.Exceptions;
import tools.vitruv.change.interaction.InteractionResultProvider;
import tools.vitruv.change.interaction.UserInteractionOptions;

/**
 * A predefined result provider that also simulates a think time on each request.
 * 
 * @author Heiko Klare
 */
@SuppressWarnings("all")
public class ThinktimeSimulatingInteractionResultProvider implements InteractionResultProvider {
  private static final Logger logger = Logger.getLogger(ThinktimeSimulatingInteractionResultProvider.class);

  private final Random random = new Random();

  private final int minWaittime;

  private final int maxWaittime;

  private final int waitTimeRange;

  private final InteractionResultProvider delegate;

  /**
   * @param minWaittime - the minimum time to wait in milliseconds
   * @param maxWaittime - the maximum time to wait in milliseconds
   */
  public ThinktimeSimulatingInteractionResultProvider(final InteractionResultProvider delegate, final int minWaittime, final int maxWaittime) {
    if ((minWaittime > maxWaittime)) {
      throw new RuntimeException(
        ((("Configure min and max waittime properly: Min" + Integer.valueOf(minWaittime)) + " Max: ") + Integer.valueOf(maxWaittime)));
    }
    this.delegate = delegate;
    this.minWaittime = minWaittime;
    this.maxWaittime = maxWaittime;
    this.waitTimeRange = (maxWaittime - minWaittime);
  }

  private void simulateUserThinktime() {
    if (((-1) < this.maxWaittime)) {
      int _nextInt = this.random.nextInt((this.waitTimeRange + 1));
      final int currentWaittime = (_nextInt + this.minWaittime);
      try {
        Thread.sleep(currentWaittime);
      } catch (final Throwable _t) {
        if (_t instanceof InterruptedException) {
          final InterruptedException e = (InterruptedException)_t;
          ThinktimeSimulatingInteractionResultProvider.logger.trace(("User think time simulation thread interrupted: " + e), e);
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    }
  }

  @Delegate
  private InteractionResultProvider waitAndAccessDelegate() {
    InteractionResultProvider _xblockexpression = null;
    {
      this.simulateUserThinktime();
      _xblockexpression = this.delegate;
    }
    return _xblockexpression;
  }

  public boolean getConfirmationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String negativeDecisionText, final String cancelDecisionText) {
    return this.waitAndAccessDelegate().getConfirmationInteractionResult(windowModality, title, message, positiveDecisionText, negativeDecisionText, cancelDecisionText);
  }

  public Iterable<Integer> getMultipleChoiceMultipleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    return this.waitAndAccessDelegate().getMultipleChoiceMultipleSelectionInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
  }

  public int getMultipleChoiceSingleSelectionInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final Iterable<String> choices) {
    return this.waitAndAccessDelegate().getMultipleChoiceSingleSelectionInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, choices);
  }

  public void getNotificationInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final UserInteractionOptions.NotificationType notificationType) {
    this.waitAndAccessDelegate().getNotificationInteractionResult(windowModality, title, message, positiveDecisionText, notificationType);
  }

  public String getTextInputInteractionResult(final UserInteractionOptions.WindowModality windowModality, final String title, final String message, final String positiveDecisionText, final String cancelDecisionText, final UserInteractionOptions.InputValidator inputValidator) {
    return this.waitAndAccessDelegate().getTextInputInteractionResult(windowModality, title, message, positiveDecisionText, cancelDecisionText, inputValidator);
  }
}
