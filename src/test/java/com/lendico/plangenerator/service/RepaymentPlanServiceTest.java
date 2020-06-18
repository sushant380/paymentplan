package com.lendico.plangenerator.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.lendico.plangenerator.domain.PlanCriteria;
import com.lendico.plangenerator.domain.RepaymentPlan;
import com.lendico.plangenerator.exception.DataException;
import com.lendico.plangenerator.service.impl.RepaymentPlanServiceImpl;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Repayment plan services test */
public class RepaymentPlanServiceTest {

  private final RepaymentPlanService repaymentPlanService = new RepaymentPlanServiceImpl();

  /** Test happy scenario */
  @Test
  public void testGetPlanService() {
    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(10)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    RepaymentPlan repaymentPlan = repaymentPlanService.getRepaymentPlan(criteria);
    assertThat(repaymentPlan).isNotNull();
  }

  /** Test if mandatory data is not present, it should throw DataException.class */
  @Test
  public void testforNullDurationGetPlanService() {
    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(null)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    Assertions.assertThrows(
        DataException.class, () -> repaymentPlanService.getRepaymentPlan(criteria));
  }

  /** Test number of payment plans should be equal to duration. */
  @Test
  public void testforDurationRecordsGetPlanService() {
    PlanCriteria criteria =
        PlanCriteria.builder()
            .duration(10)
            .loanAmount(BigDecimal.valueOf(2000))
            .nominalRate(BigDecimal.valueOf(5))
            .statDate(new Date())
            .build();
    RepaymentPlan plan = repaymentPlanService.getRepaymentPlan(criteria);
    assertThat(criteria.getDuration()).isEqualTo(plan.getTotal().intValue());
  }
}
