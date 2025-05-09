package fintrek.command.budget;

import fintrek.budget.core.BudgetManager;
import fintrek.command.registry.CommandResult;
import fintrek.expense.core.RegularExpenseManager;
import fintrek.misc.MessageDisplayer;
import fintrek.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class BudgetCommandTest {
    public static final String COMMAND_NAME = "budget";
    private static final RegularExpenseManager regularExpenseManager =
            RegularExpenseManager.getInstance();
    private static final BudgetManager budgetManager = BudgetManager.getInstance();

    /**
     * Clear all existing expenses in regularExpenseManager before each test.
     */
    @BeforeEach
    public void setUp() {
        regularExpenseManager.clear();
    }

    /**
     * Tests if BudgetCommand is able to set monthly budgets given valid inputs
     * @param input valid inputs in the form of $[AMOUNT] where AMOUNT is positive
     */
    @ParameterizedTest
    @ValueSource(strings = {
        "$  2.00", "$2.41", "    $3.14",
        "$3", "$0.50", "$1.50", "$200.00", "$350.00", "$231"
    })
    public void testBudgetCommandValidInput(String input) {
        BudgetCommand budgetCommand = new BudgetCommand(false);
        CommandResult result = budgetCommand.execute(input);

        TestUtils.assertCommandSuccess(result, input);
    }

    /**
     * Verifies if calling the budget command with an empty input or whitespaces only
     * returns an error message
     * @param input empty input or inputs consisting of only whitespaces
     */
    @ParameterizedTest
    @ValueSource (strings = {"", "             ", "  "})
    public void testBudgetCommandEmptyInput(String input) {
        BudgetCommand budgetCommand = new BudgetCommand(false);
        CommandResult result = budgetCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        TestUtils.assertCommandMessage(result, input,
                String.format(MessageDisplayer.ARG_EMPTY_MESSAGE_TEMPLATE, COMMAND_NAME));
    }

    /**
     * Tests if BudgetCommand returns error messages for invalid inputs
     * not in the format of $[AMOUNT]
     * @param input invalid inputs in various wrong formats
     */
    @ParameterizedTest
    @ValueSource (strings = {"65", "HI!", "CS2113", "$          S5.78", "$CS3230. 98", "Hello $5"})
    public void testBudgetInvalidFormatInput(String input) {
        BudgetCommand budgetCommand = new BudgetCommand(false);
        CommandResult result = budgetCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        TestUtils.assertCommandMessage(result, input,
               String.format(MessageDisplayer.INVALID_FORMAT_MESSAGE_TEMPLATE, COMMAND_NAME));
    }

    /**
     * Verifies that adding a budget with an invalid amount will
     * return an appropriate error message
     * @param input a String containing an invalid amount
     */
    @ParameterizedTest
    @ValueSource (strings = {"$0"})
    public void testBudgetInvalidAmount(String input) {
        BudgetCommand budgetCommand = new BudgetCommand(false);
        CommandResult result = budgetCommand.execute(input);

        TestUtils.assertCommandFailure(result, input);
        TestUtils.assertCommandMessage(result, input, MessageDisplayer.INVALID_AMT_MESSAGE);
    }
}
