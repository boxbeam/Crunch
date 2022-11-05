package redempt.crunch.solve;

import org.junit.jupiter.api.Test;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;

import static org.junit.jupiter.api.Assertions.*;

public class CrunchSolveTest {

    @Test
    public void invalidEquationTest() {
        assertDoesNotThrow(() -> new EquationSolver(Crunch.compileExpression("2*$1 = 4")));
        assertThrows(IllegalArgumentException.class, () -> new EquationSolver(Crunch.compileExpression("2*$1 + 4")));
        assertThrows(IllegalArgumentException.class, () -> new EquationSolver(Crunch.compileExpression("2*$1 = $2")));
    }

}