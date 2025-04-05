package fintrek.command.delete;

import fintrek.command.registry.CommandResult;
import fintrek.expense.core.Expense;
import fintrek.expense.core.RegularExpenseManager;
import fintrek.expense.service.ExpenseService;
import fintrek.misc.MessageDisplayer;
import fintrek.util.TestUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static fintrek.expense.service.AppServices.REGULAR_SERVICE;

public class DeleteCommandTest {
    private ExpenseService service;

    /**
     * Clears all existing expenses in RegularExpenseManager before each test.
     */
    @BeforeEach
    public void setUp() {
        RegularExpenseManager.getInstance().clear();
        service = REGULAR_SERVICE;
        TestUtils.addConstantExpenses();
    }

    @Test
    public void testDeleteCommandEmptyIndex() {
        DeleteCommand deleteCommand = new DeleteCommand(false);
        CommandResult result = deleteCommand.execute("");

        TestUtils.assertCommandFailure(result, "");
        TestUtils.assertCommandMessage(result, "", MessageDisplayer.IDX_EMPTY_MESSAGE);
    }

    /**
     * Verifies that invoking the delete command with various invalid forms of indices
     * returns an error message
     * @param input a String of the form of an invalid index
     */
    @ParameterizedTest
    @ValueSource(strings = {"invalid", "0.99", "2.", "1.2.3", "-1", "0"})
    public void testDeleteCommandInvalidIndex(String input) {
        DeleteCommand deleteCommand = new DeleteCommand(false);
        CommandResult result = deleteCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        TestUtils.assertCommandMessage(result, input, MessageDisplayer.INVALID_IDX_FORMAT_MESSAGE);
    }

    /**
     * Verifies that attempting to delete an expense at an index beyond the current list size
     * returns an appropriate error message.
     * @param input a String containing a large number beyond the size of the list
     */
    @ParameterizedTest
    @ValueSource(strings = {"999"}) // Assuming <999 expenses exist
    public void testDeleteCommandOutOfBounds(String input) {
        DeleteCommand deleteCommand = new DeleteCommand(false);
        CommandResult result = deleteCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        TestUtils.assertCommandMessage(result, input, MessageDisplayer.IDX_OUT_OF_BOUND_MESSAGE);
    }

    @Test
    public void testDeleteCommandValidInput() {
        DeleteCommand deleteCommand = new DeleteCommand(false);
        int expectedSize = service.countExpenses() - 1;
        Expense removedExpense = service.getExpense(0);
        String expenseStr = '"' + removedExpense.toString() + '"';

        CommandResult result = deleteCommand.execute("1");

        TestUtils.assertCommandSuccess(result, "1");

        String expectedMessage = String.format(MessageDisplayer.DELETE_SUCCESS_MESSAGE_TEMPLATE,
                expenseStr, expectedSize);
        TestUtils.assertCommandMessage(result, "1", expectedMessage);

        // Re-validate count from service
        assert expectedSize == service.countExpenses();
    }
}
