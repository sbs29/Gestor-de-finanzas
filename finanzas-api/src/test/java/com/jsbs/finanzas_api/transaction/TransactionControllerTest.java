package com.jsbs.finanzas_api.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jsbs.finanzas_api.auth.PagedResponse;
import com.jsbs.finanzas_api.category.CategoryType;
import com.jsbs.finanzas_api.common.exception.TransactionNotFoundException;
import com.jsbs.finanzas_api.security.JwtService;
import com.jsbs.finanzas_api.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Test
    @WithMockUser
    void shouldReturnBadRequestWhenTransactionRequestIsInvalid() throws Exception {

        TransactionRequest request = new TransactionRequest(
                null,
                "",
                null,
                null
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.amount").value("El importe es obligatorio"))
                .andExpect(jsonPath("$.errors.description").value("La descripción es obligatoria"))
                .andExpect(jsonPath("$.errors.date").value("La fecha es obligatoria"))
                .andExpect(jsonPath("$.errors.categoryId").value("La categoría es obligatoria"));

        verify(transactionService, never()).createTransaction(any());
    }


    @Test
    void shouldReturnUnauthorizedWhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/transactions"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedWhenCreatingTransactionWithoutAuthentication()
            throws Exception {

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("100.00"),
                "Supermercado",
                LocalDateTime.now(),
                1L
        );

        mockMvc.perform(post("/api/transactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        verify(transactionService, never()).createTransaction(any());
    }


    @Test
    @WithMockUser
    void shouldReturnTransactionById() throws Exception {

        TransactionResponse response = new TransactionResponse(
                1L,
                new BigDecimal("100.00"),
                "Supermercado",
                LocalDateTime.now(),
                1L,
                "Comida",
                CategoryType.EXPENSE
        );

        when(transactionService.getTransactionById(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/transactions/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.description")
                        .value("Supermercado"))
                .andExpect(jsonPath("$.categoryName")
                        .value("Comida"))
                .andExpect(jsonPath("$.categoryType")
                        .value("EXPENSE"));

        verify(transactionService).getTransactionById(1L);
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundWhenTransactionDoesNotExist()
            throws Exception {

        when(transactionService.getTransactionById(999L))
                .thenThrow(new TransactionNotFoundException(999L));

        mockMvc.perform(get("/api/transactions/999"))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(transactionService)
                .getTransactionById(999L);
    }

    @Test
    @WithMockUser
    void shouldDeleteTransaction() throws Exception {

        mockMvc.perform(delete("/api/transactions/1")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransactionById(1L);

    }

    @Test
    @WithMockUser
    void shouldUpdateTransaction() throws Exception {

        TransactionRequest request = new TransactionRequest(
                new BigDecimal("150.00"),
                "Supermercado actualizado",
                LocalDateTime.now(),
                1L
        );

        TransactionResponse response = new TransactionResponse(
                1L,
                new BigDecimal("150.00"),
                "Supermercado actualizado",
                request.date(),
                1L,
                "Comida",
                CategoryType.EXPENSE
        );

        when(transactionService.updateTransaction(eq(1L), any(TransactionRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/transactions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.description").value("Supermercado actualizado"))
                .andExpect(jsonPath("$.categoryName").value("Comida"))
                .andExpect(jsonPath("$.categoryType").value("EXPENSE"));

        verify(transactionService)
                .updateTransaction(eq(1L), any(TransactionRequest.class));
    }

    @Test
    @WithMockUser
    void shouldReturnRequestWhenUpdatingTransactionWithInvalidData() throws Exception {

        TransactionRequest request = new TransactionRequest(
                null,
                "",
                null,
                null
        );

        mockMvc.perform(put("/api/transactions/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Validation failed"));

        verify(transactionService, never()).updateTransaction(anyLong(), any(TransactionRequest.class));

    }

}
