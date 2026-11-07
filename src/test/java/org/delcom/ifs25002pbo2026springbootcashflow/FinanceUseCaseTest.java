package org.delcom.ifs25002pbo2026springbootcashflow;

import org.delcom.ifs25002pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25002pbo2026springbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.entity.SortOption;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25002pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Unit test logika use case, repository, dan presenter (tanpa Spring/Vaadin). */
class FinanceUseCaseTest {

    private FinanceUseCase useCase;
    private final FinancePresenter presenter = new FinancePresenter();

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void balanceFollowsIncomeAndExpense() throws Exception {
        useCase.addIncome("Gaji", "5000000");
        useCase.addExpense("Makan", "25000");
        assertEquals(0, new BigDecimal("4975000").compareTo(useCase.getBalance()));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(useCase.getBalance()));
    }

    @Test
    void removeUpdatesBalanceAndThrowsWhenMissing() throws Exception {
        Transaction income = useCase.addIncome("Gaji", "1000");
        useCase.removeTransaction(income.getId());
        assertEquals(0, BigDecimal.ZERO.compareTo(useCase.getBalance()));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(99));
    }

    @Test
    void validationRejectsBadInput() {
        assertThrows(ValidationException.class, () -> useCase.addIncome("  ", "1000"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "-5"));
        assertThrows(ValidationException.class, () -> useCase.addExpense("Makan", "0"));
        assertThrows(ValidationException.class, () -> useCase.addIncome("Gaji", "abc"));
        assertThrows(ValidationException.class, () -> useCase.addIncome("Gaji", ""));
        assertTrue(useCase.getAllTransactions().isEmpty());
    }

    @Test
    void searchIsCaseInsensitiveAndDoesNotMutate() throws Exception {
        useCase.addIncome("Gaji Bulanan", "100");
        useCase.addExpense("Makan Siang", "10");
        assertEquals(1, useCase.searchTransactions("gaji").size());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void sortOptionsWork() throws Exception {
        useCase.addExpense("A", "30");
        useCase.addIncome("B", "10");
        useCase.addIncome("C", "20");
        assertEquals("B", useCase.sortTransactions(SortOption.AMOUNT_ASC).get(0).getDescription());
        assertEquals("A", useCase.sortTransactions(SortOption.AMOUNT_DESC).get(0).getDescription());
        assertTrue(useCase.sortTransactions(SortOption.INCOME_FIRST).get(0).isIncome());
        assertTrue(useCase.sortTransactions(SortOption.EXPENSE_FIRST).get(0).isExpense());
    }

    @Test
    void findAllReturnsDefensiveCopy() throws Exception {
        useCase.addIncome("Gaji", "100");
        List<Transaction> copy = useCase.getAllTransactions();
        copy.clear();
        assertEquals(1, useCase.getAllTransactions().size());
    }

    @Test
    void presenterFormatsMoneyAndLine() throws Exception {
        Transaction t = useCase.addIncome("Gaji", "1000000");
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(t.getAmount()));
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(t));
    }
}
