package com.lendico.plangenerator.service;

import com.lendico.plangenerator.domain.PlanCriteria;
import com.lendico.plangenerator.domain.RepaymentPlan;

public interface RepaymentPlanService {
  RepaymentPlan getRepaymentPlan(PlanCriteria criteria);
}
