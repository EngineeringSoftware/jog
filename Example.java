import jog.api.*;

import static jog.api.Action.*;

public class Example {

    @Pattern
    public void ADD2(long a, long b, long c, long d) {
        before((a - b) + (c - d));
        after((a + c) - (b + d));
    }

    @Pattern
    public void ADD7(long a, long b, long c) {
        before((a - b) + (b - c));
        after(a - c);
    }
}
