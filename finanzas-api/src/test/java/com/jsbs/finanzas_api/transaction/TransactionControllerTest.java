package com.jsbs.finanzas_api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsbs.finanzas_api.auth.PagedResponse;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.security.JwtService;
import com.jsbs.finanzas_api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser
    void shouldReturnPagedTransactions() throws Exception {

        PagedResponse<TransactionResponse> response = new PagedResponse<>(
                List.of(),
                0,
                5,
                0,
                0,
                true
        );

        when(transactionService.getAllTransactions(any(Pageable.class), isNull(), isNull(), isNull()))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.last").value(true));
    }


    @Test
    @WithMockUser
    void shouldCreateTransaction() throws Exception {

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"),
                "Supermercado",
                LocalDateTime.now(),
                1L
        );

        TransactionResponse response = new TransactionResponse(
                1L,
                new BigDecimal("100.00"),
                "Supermercado",
                request.date(),
                1L,
                "Comida",
                CategoryType.EXPENSE
        );

        when(transactionService.createTransaction(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description")
                        .value("Supermercado"));
    }
}
