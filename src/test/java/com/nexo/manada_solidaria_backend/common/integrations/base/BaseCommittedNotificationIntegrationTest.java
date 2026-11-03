package com.nexo.manada_solidaria_backend.common.integrations.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/** Tests which intentionally commit use their own H2 database and clean it after every case. */
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:notification-events")
public abstract class BaseCommittedNotificationIntegrationTest extends BaseAuthenticatedIntegrationTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @MockitoBean(name = "notificationExecutor")
    private Executor notificationExecutor;

    @BeforeEach
    void configureNotificationExecutor() {
        // A production worker has its own transaction. A synchronous worker must not
        // join the transaction whose AFTER_COMMIT callbacks are currently running.
        TransactionTemplate workerTransaction = new TransactionTemplate(transactionManager);
        workerTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            workerTransaction.executeWithoutResult(status -> task.run());
            return null;
        }).when(notificationExecutor).execute(any(Runnable.class));
    }

    protected void commitAndStartReadTransaction() {
        TestTransaction.flagForCommit();
        TestTransaction.end();
        // Keep lazy associations available for assertions, using a fresh persistence context.
        TestTransaction.start();
    }

    @AfterEach
    void cleanCommittedData() throws Exception {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForRollback();
            TestTransaction.end();
        }
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            if (!connection.getMetaData().getURL().equals("jdbc:h2:mem:notification-events")) {
                throw new IllegalStateException("Cleanup requires the dedicated notification test database");
            }
            var tables = new ArrayList<String>();
            try (var result = statement.executeQuery("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
                    + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_TYPE = 'BASE TABLE'")) {
                while (result.next()) {
                    tables.add(result.getString(1));
                }
            }
            statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            try {
                for (String table : tables) {
                    statement.execute("TRUNCATE TABLE \"" + table.replace("\"", "\"\"") + "\"");
                }
            } finally {
                statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
            }
        }
    }
}
