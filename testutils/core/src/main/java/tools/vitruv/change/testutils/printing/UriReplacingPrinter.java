package tools.vitruv.change.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import org.eclipse.emf.common.util.URI;

public class UriReplacingPrinter implements ModelPrinter {
  private final ModelPrinter subPrinter;

  private final List<Map.Entry<URI, URI>> replacements;

  private UriReplacingPrinter(final ModelPrinter subPrinter, final List<Map.Entry<URI, URI>> replacements) {
    this.subPrinter = subPrinter;
    this.replacements = replacements;
  }

  public UriReplacingPrinter(final List<Map.Entry<URI, URI>> replacements) {
    this.subPrinter = this;
    this.replacements = UriReplacingPrinter.checkReplacements(replacements);
  }

  private static List<Map.Entry<URI, URI>> checkReplacements(final List<Map.Entry<URI, URI>> replacements) {
    for (final Map.Entry<URI, URI> entry : replacements) {
      for (final URI usedUri : List.of(entry.getKey(), entry.getValue())) {
        boolean _isPrefix = usedUri.isPrefix();
        Preconditions.checkArgument(_isPrefix, "This is not a prefix URI: " + usedUri);
      }
    }
    return replacements;
  }

  @Override
  public PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (object instanceof URI) {
      _matched = true;
      PrintResult _xblockexpression = null;
      {
        final URI replacement = this.replacementFor(((URI) object));
        PrintResult _xifexpression = null;
        if ((replacement != null)) {
          _xifexpression = this.subPrinter.printObject(target, idProvider, replacement);
        } else {
          _xifexpression = PrintResult.NOT_RESPONSIBLE;
        }
        _xblockexpression = _xifexpression;
      }
      _switchResult = _xblockexpression;
    }
    if (!_matched) {
      _switchResult = PrintResult.NOT_RESPONSIBLE;
    }
    return _switchResult;
  }

  @Override
  public PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider,
      final Object object) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (object instanceof URI) {
      _matched = true;
      PrintResult _xblockexpression = null;
      {
        final URI replacement = this.replacementFor(((URI) object));
        PrintResult _xifexpression = null;
        if ((replacement != null)) {
          _xifexpression = this.subPrinter.printObjectShortened(target, idProvider, replacement);
        } else {
          _xifexpression = PrintResult.NOT_RESPONSIBLE;
        }
        _xblockexpression = _xifexpression;
      }
      _switchResult = _xblockexpression;
    }
    if (!_matched) {
      _switchResult = PrintResult.NOT_RESPONSIBLE;
    }
    return _switchResult;
  }

  private URI replacementFor(final URI uri) {
    return this.replacements.stream()
        .map(it -> uri.replacePrefix(it.getKey(), it.getValue()))
        .filter(it -> it != null)
        .findFirst()
        .orElse(null);
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
    return new UriReplacingPrinter(subPrinter, this.replacements);
  }
}
