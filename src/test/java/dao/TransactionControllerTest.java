package dao;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import com.sun.tools.internal.ws.processor.model.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.sql.DataSource;

import static junit.framework.TestCase.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("dev")
@ContextConfiguration(locations = {
        "file:src/test/resourcers/config/app-config.xml",
        "file:src/test/resourcers/config/test-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class TransactionControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private DataSource dataSource;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("delete from accounts");
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testAccountControllerAdd() throws Exception {
        mockMvc.perform(post("/add")
                .param("firstname", "Test-12")
                .param("lastname", "Test-12"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("accounts"))
                .andExpect(model().size(1));
        Account testAccound = transactionService.getAllAccount().get(1);
        assertTrue("isTrue",
                (testAccound.getFirstname().equals("Test-12") && testAccound.getLastname().equals("Test-12")));
    }

    @Test
    public void testMainController() throws Exception {

        Account account = new Account();
        account.setFirstname("Test1");
        account.setLastname("Test1");

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("accounts"));
    }

    @Test
    public void testAccountControllerDelete() throws Exception {
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(post("/delete")
                    .param("id", String.valueOf(i)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Account deleted"));
        }
    }

    @Test
    public void testAccountControllerDeleteInvalidId() throws Exception {
        mockMvc.perform(post("/delete")
                .param("id", "12"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Account not found"));
    }



    @Test
    public void testTransactionControllerAdditionalSum() throws Exception {
        mockMvc.perform(get("/additionalSum")
                .param("id", "11")
                .param("sum", "3000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account updated"));
        Account testAccound = transactionService.getAllAccount().get(1);
        Integer balance = testAccound.getBalance();
        assertTrue("isTrue", balance.equals(3000));
    }

    @Test
    public void testTransactionControllerAdditionalSumInvalidId() throws Exception {
        mockMvc.perform(get("/additionalSum")
                .param("id", "12")
                .param("sum", "3000"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Account not found"));
    }

    @Test
    public void testTransactionControllerDebitSum() throws Exception {
        mockMvc.perform(get("/debitSum")
                .param("id", "11")
                .param("sum", "2000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        Account testAccound = transactionService.getAllAccount().get(1);
        Integer balance = testAccound.getBalance();
        assertTrue("isTrue", balance.equals(1000));
        mockMvc.perform(get("/debitSum")
                .param("id", "11")
                .param("sum", "1000"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction completed"));
        Account testAccound2 = transactionService.getAllAccount().get(1);
        Integer balance2 = testAccound2.getBalance();
        assertTrue("isTrue", balance2.equals(0));
    }

    @Test
    public void testTransactionControllerDebitSumInvalidSum() throws Exception {
        mockMvc.perform(get("/debitSum")
                .param("id", "11")
                .param("sum", "1000"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Transaction completed"));
        Account testAccound = transactionService.getAllAccount().get(1);
        Integer balance = testAccound.getBalance();
        assertTrue("isTrue", balance.equals(0));
    }

    @Test
    public void testTransactionControllerDebitSumInvalidId() throws Exception {
        mockMvc.perform(get("/debitSum")
                .param("id", "12")
                .param("sum", "1000"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Account not found"));
    }

    @Test
    public void testTransactionControllerDoTransfer() throws Exception {
        mockMvc.perform(get("/doTransfer"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTransactionControllerDoTransferInvalidSum() throws Exception {
        mockMvc.perform(get("/doTransfer"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTransactionControllerDoTransferInvalidId() throws Exception {
        mockMvc.perform(get("/doTransfer"))
                .andExpect(status().isOk());
    }
}


