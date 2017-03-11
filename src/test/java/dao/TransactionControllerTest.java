package dao;

import com.kirilov.model.Account;
import com.kirilov.service.TransactionService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "file:src/test/resourcers/config/applicationContext.xml",
        "file:src/test/resourcers/config/dispatcher-servlet.xml"})
@WebAppConfiguration
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class TransactionControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TransactionService transactionService;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        transactionService.addAccount(new Account());
        Account account;
        for (int i = 0; i < 10; i++) {
            account = new Account();
            account.setFirstname("TEST" + 1);
            account.setFirstname("TEST" + 2);
            transactionService.addAccount(account);
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMainController() throws Exception {

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(model().size(10))
                .andExpect(model().attributeExists("accounts"));
    }

    @Test
    public void testAccountControllerAdd() throws Exception {
        mockMvc.perform(post("/add")
                .param("firstName", "test1")
                .param("lastName", "test1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account created"));
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_HTML))
                .andExpect(model().size(11))
                .andExpect(model().attributeExists("accounts"));
    }

    @Test
    public void testAccountControllerDelete() throws Exception {
        for (int i = 0; i < 11; i++) {
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
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted"));
    }


    @Test
    public void testTransactionControllerDebitSum() throws Exception {
        mockMvc.perform(get("/debitSum"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTransactionControllerDebitSumInvalidSum() throws Exception {
        mockMvc.perform(get("/debitSum"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTransactionControllerDebitSumInvalidId() throws Exception {
        mockMvc.perform(get("/debitSum"))
                .andExpect(status().isOk());
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

    @Test
    public void testTransactionControllerAdditionalSum() throws Exception {
        mockMvc.perform(get("/additionalSum"))
                .andExpect(status().isOk());
    }

    @Test
    public void testTransactionControllerAdditionalSumInvalidId() throws Exception {
        mockMvc.perform(get("/additionalSum"))
                .andExpect(status().isOk());
    }
}

