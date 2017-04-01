package dao;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import org.junit.*;
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

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@ContextConfiguration(locations = {
        "file:src/test/resourcers/config/test-context.xml",
        "file:src/test/resourcers/config/test-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TransactionService transactionService;

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

    private void addAccount() {
        Account account = new Account();
        account.setFirstname("Test1");
        account.setLastname("Test1");
        transactionService.addAccount(account);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAccountControllerAdd() throws Exception {
        mockMvc.perform(post("/add")
                .param("firstname", "firstAccount")
                .param("lastname", "firstAccount"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().size(1));
        Account testAccound = transactionService.getAllAccount().get(0);
        assertTrue("Account incorrect", (testAccound.getFirstname().equals("firstAccount") && testAccound.getLastname().equals("firstAccount")));
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerAdditionalSumInvalidId() throws Exception {
        mockMvc.perform(post("/additionalSum")
                .param("id", "1")
                .param("sum", "3000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account not found"));
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerAdditionalSum() throws Exception {
        addAccount();
        mockMvc.perform(post("/additionalSum")
                .param("id", "1")
                .param("sum", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        Account account = transactionService.getAllAccount().get(0);
        Integer balance = account.getBalance();
        assertTrue("Sum incorrect", balance.equals(3000));
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testMainController() throws Exception {
        for (int i = 0; i < 10; i++) {
            addAccount();
        }
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().size(1));
        assertTrue("Results incorrect", transactionService.getAllAccount().size() == 10);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAccountControllerDelete() throws Exception {
        addAccount();
        mockMvc.perform(post("/delete")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted"));
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testAccountControllerDeleteInvalidId() throws Exception {
        mockMvc.perform(post("/delete")
                .param("id", "1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account not found"));
    }


    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDebitSum() throws Exception {
        addAccount();
        mockMvc.perform(post("/additionalSum")
                .param("id", "1")
                .param("sum", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        mockMvc.perform(post("/debitSum")
                .param("id", "1")
                .param("sum", "2000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 1000);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDebitSumInvalidSum() throws Exception {
        addAccount();
        mockMvc.perform(post("/debitSum")
                .param("id", "1")
                .param("sum", "2000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("On account id 1 has insufficient funds"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 0);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDebitSumInvalidId() throws Exception {
        addAccount();
        mockMvc.perform(post("/debitSum")
                .param("id", "3")
                .param("sum", "2000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account not found"));
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDoTransfer() throws Exception {
        addAccount();
        addAccount();
        mockMvc.perform(post("/additionalSum")
                .param("id", "1")
                .param("sum", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 3000);
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(1).getBalance() == 0);
        mockMvc.perform(post("/doTransfer")
                .param("fromid", "1")
                .param("toid", "2")
                .param("sum", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 0);
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(1).getBalance() == 3000);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDoTransferInvalidSum() throws Exception {
        addAccount();
        addAccount();
        mockMvc.perform(post("/doTransfer")
                .param("fromid", "1")
                .param("toid", "2")
                .param("sum", "3000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("On account id 1 has insufficient funds"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 0);
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(1).getBalance() == 0);
    }

    @Test
    @Sql(scripts = "/sql/create_schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/drop_schema.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testTransactionControllerDoTransferInvalidId() throws Exception {
        addAccount();
        mockMvc.perform(post("/doTransfer")
                .param("fromid", "1")
                .param("toid", "2")
                .param("sum", "3000"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Account not found"));
        assertTrue("Balance incorrect", transactionService.getAllAccount().get(0).getBalance() == 0);
    }
}


