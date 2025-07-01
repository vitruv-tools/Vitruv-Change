package tools.vitruv.change.atomic.message;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Error {
    public static final String UNKNOWN_CHANGE_OF_TYPE = "trying to copy unknown change of type %s";
}
