package com.lendico.plangenerator.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lendico.plangenerator.domain.Payment;
import com.lendico.plangenerator.domain.PlanCriteria;
import com.lendico.plangenerator.domain.RepaymentPlan;
import com.lendico.plangenerator.service.RepaymentPlanService;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;

@WebMvcTest(PlanController.class)
public class PlanGeneratorApplicationTests {

  @MockBean RepaymentPlanService service;
  private final ObjectMapper mapper = new ObjectMapper();
  @Autowired private MockMvc mockMvc;

  /**
   * Check happy scenario.
   *
   * @throws Exception
   */
  @Test
  public void getPlanShouldReturnListPlanFromService() throws Exception {
    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(10)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    Payment payment =
        Payment.builder()
            .borrowerPaymentAmount(BigDecimal.valueOf(220))
            .date(new Date())
            .remainingOutstandingPrincipal(BigDecimal.valueOf(1780))
            .initialOutstandingPrincipal(BigDecimal.valueOf(2000))
            .principal(BigDecimal.valueOf(200))
            .interest(BigDecimal.valueOf(5))
            .build();
    RepaymentPlan repaymentPlan =
        RepaymentPlan.builder().borrowerPayments(Arrays.asList(payment)).total(1L).build();
    when(service.getRepaymentPlan(criteria)).thenReturn(repaymentPlan);
    this.mockMvc
        .perform(
            post("/plans")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("test:test".getBytes()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(criteria)))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              RepaymentPlan actualObject = mapper.readValue(json, RepaymentPlan.class);
              assertThat(repaymentPlan.getTotal()).isEqualTo(actualObject.getTotal());
            });
  }

  /**
   * Check when authorization header not sent with request it should not allow user to access the
   * api
   *
   * @throws Exception
   */
  @Test
  void whenAuthorizationNotUsedThenReturns401ndErrorResult() throws Exception {

    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(null)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    mockMvc
        .perform(
            post("/plans")
                .contentType("application/json")
                .content(mapper.writeValueAsString(criteria)))
        .andExpect(status().isUnauthorized());
  }

  /**
   * Check when mandatory values are missing, it should throw bad request(400) status.
   *
   * @throws Exception
   */
  @Test
  void whenNullValueThenReturns400() throws Exception {

    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(null)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();

    mockMvc
        .perform(
            post("/plans")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("test:test".getBytes()))
                .contentType("application/json")
                .content(mapper.writeValueAsString(criteria)))
        .andExpect(status().isBadRequest());
  }

  /**
   * Check error message details when bad request status is thrown to get details about the error to
   * consumer.
   *
   * @throws Exception
   */
  @Test
  void whenNullValueThenReturns400AndErrorResult() throws Exception {

    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(null)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    mockMvc
        .perform(
            post("/plans")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("test:test".getBytes()))
                .contentType("application/json")
                .content(mapper.writeValueAsString(criteria)))
        .andExpect(status().isBadRequest())
        .andExpect(
            mvcResult -> {
              String json = mvcResult.getResponse().getContentAsString();
              JsonNode node = mapper.readValue(json, JsonNode.class);
              assertThat("Duration is mandatory. e.g. duration:12")
                  .isEqualToIgnoringCase(node.findValue("errors").get(0).asText());
            });
  }
}
