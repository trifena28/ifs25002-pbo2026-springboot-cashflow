package org.delcom.ifs25002pbo2026springbootcashflow;

import org.delcom.ifs25002pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25002pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25002pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Smoke test: memastikan wiring bean Clean Architecture berjalan
 * dan App.main mendelegasikan ke SpringApplication.run.
 */
@SpringBootTest
class AppTest {

    @Autowired
    private ITransactionRepository transactionRepository;

    @Autowired
    private FinanceUseCase financeUseCase;

    @Autowired
    private FinancePresenter financePresenter;

    @Test
    void contextLoadsAndBeansAreWired() {
        assertNotNull(transactionRepository);
        assertNotNull(financeUseCase);
        assertNotNull(financePresenter);
    }

    @Test
    void mainDelegatesToSpringApplicationRun() throws Exception {
        String[] args = new String[0];
        try (MockedStatic<SpringApplication> springApplication = Mockito.mockStatic(SpringApplication.class)) {
            App.main(args);
            springApplication.verify(() -> SpringApplication.run(App.class, args));
        }
    }
}
