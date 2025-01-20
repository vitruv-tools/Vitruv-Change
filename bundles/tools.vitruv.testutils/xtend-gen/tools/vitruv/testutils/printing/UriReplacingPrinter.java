package tools.vitruv.testutils.printing;

import com.google.common.base.Preconditions;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.lib.Pair;

@SuppressWarnings("all")
public class UriReplacingPrinter implements ModelPrinter {
  private final ModelPrinter subPrinter;

  private final List<Pair<URI, URI>> replacements;

  private UriReplacingPrinter(final ModelPrinter subPrinter, final List<Pair<URI, URI>> replacements) {
    this.subPrinter = subPrinter;
    this.replacements = replacements;
  }

  public UriReplacingPrinter(final List<Pair<URI, URI>> replacements) {
    this.subPrinter = this;
    this.replacements = UriReplacingPrinter.checkReplacements(replacements);
  }

  private static List<Pair<URI, URI>> checkReplacements(final List<Pair<URI, URI>> replacements) {
    List<Pair<URI, URI>> _xblockexpression = null;
    {
      final Function1<Pair<URI, URI>, List<URI>> _function = (Pair<URI, URI> it) -> {
        return List.<URI>of(it.getKey(), it.getValue());
      };
      Iterable<URI> _flatMap = IterableExtensions.<Pair<URI, URI>, URI>flatMap(replacements, _function);
      for (final URI usedUri : _flatMap) {
        boolean _isPrefix = usedUri.isPrefix();
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("This is not a prefix URI: ");
        _builder.append(usedUri);
        Preconditions.checkArgument(_isPrefix, _builder);
      }
      _xblockexpression = replacements;
    }
    return _xblockexpression;
  }

  @Override
  public PrintResult printObject(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (object instanceof URI) {
      _matched=true;
      PrintResult _xblockexpression = null;
      {
        final URI replacement = this.replacementFor(((URI)object));
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
  public PrintResult printObjectShortened(final PrintTarget target, final PrintIdProvider idProvider, final Object object) {
    PrintResult _switchResult = null;
    boolean _matched = false;
    if (object instanceof URI) {
      _matched=true;
      PrintResult _xblockexpression = null;
      {
        final URI replacement = this.replacementFor(((URI)object));
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
    final Function1<Pair<URI, URI>, URI> _function = (Pair<URI, URI> it) -> {
      return uri.replacePrefix(it.getKey(), it.getValue());
    };
    final Function1<URI, Boolean> _function_1 = (URI it) -> {
      return Boolean.valueOf((it != null));
    };
    return IterableExtensions.<URI>findFirst(ListExtensions.<Pair<URI, URI>, URI>map(this.replacements, _function), _function_1);
  }

  @Override
  public ModelPrinter withSubPrinter(final ModelPrinter subPrinter) {
    return new UriReplacingPrinter(subPrinter, this.replacements);
  }
}
