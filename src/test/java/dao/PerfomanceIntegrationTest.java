package dao;

import com.kirilov.dao.AccountDao;
import com.kirilov.service.TransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@ContextConfiguration(locations = {
        "file:src/test/resourcers/config/test-context.xml",
        "file:src/test/resourcers/config/test-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PerfomanceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;
    private Connection connection;

    @Before
    public void setUp() throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() {
    }

    @PostConstruct
    private void postConstruct() throws SQLException {
        connection = dataSource.getConnection();
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void addedProcess() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/additionalSum")
                            .param("id", "1")
                            .param("sum", "50"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorService::submit);

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        assertEquals(6000, accountDao.findbyId(1).getBalance());
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void debitProcess() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        IntStream.range(0, 50).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/debitSum")
                            .param("id", "1")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorService::submit);

        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        assertEquals(10, accountDao.findbyId(1).getBalance());
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert_transfer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void transferProcess() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/doTransfer")
                            .param("fromid", "1")
                            .param("toid", "2")
                            .param("sum", "50"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorService::submit);
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        assertEquals(0, accountDao.findbyId(1).getBalance());
        assertEquals(3000, accountDao.findbyId(2).getBalance());
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/insert_all_process.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void allProcess() throws Exception {
        ExecutorService executorServiceAdded = Executors.newFixedThreadPool(32);
        ExecutorService executorServiceDebit = Executors.newFixedThreadPool(32);
        ExecutorService executorServiceTransaction = Executors.newFixedThreadPool(32);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/additionalSum")
                            .param("id", "1")
                            .param("sum", "100"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceAdded::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/additionalSum")
                            .param("id", "2")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceAdded::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/additionalSum")
                            .param("id", "3")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceAdded::submit);
//
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/debitSum")
                            .param("id", "1")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceDebit::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/debitSum")
                            .param("id", "2")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceDebit::submit);
//
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/doTransfer")
                            .param("fromid", "1")
                            .param("toid", "2")
                            .param("sum", "50"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceTransaction::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/doTransfer")
                            .param("fromid", "1")
                            .param("toid", "3")
                            .param("sum", "10"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceTransaction::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/doTransfer")
                            .param("fromid", "1")
                            .param("toid", "2")
                            .param("sum", "30"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceTransaction::submit);
        IntStream.range(0, 100).mapToObj(i -> new Runnable() {
            @Override
            public void run() {
                try {
                    mockMvc.perform(post("/doTransfer")
                            .param("fromid", "2")
                            .param("toid", "3")
                            .param("sum", "10"))
                            .andExpect(status().isOk())
                            .andExpect(content().string("Transaction completed"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).parallel().forEach(executorServiceTransaction::submit);
        executorServiceAdded.shutdown();
        executorServiceDebit.shutdown();
        executorServiceTransaction.shutdown();
        try {
            executorServiceAdded.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            executorServiceDebit.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            executorServiceTransaction.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
    }
}

