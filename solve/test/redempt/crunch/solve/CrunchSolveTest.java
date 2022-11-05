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

        CompiledExpression expr = Crunch.compileExpression("2^$1 = 1024");
        EquationSolver solver = new EquationSolver(expr);
        double value = solver.solve();
        System.out.println(value);
        System.out.println(solver.error(value));
    }

}