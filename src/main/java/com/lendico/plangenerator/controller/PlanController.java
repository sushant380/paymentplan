package com.lendico.plangenerator.controller;

import com.lendico.plangenerator.domain.PlanCriteria;
import com.lendico.plangenerator.domain.RepaymentPlan;
import com.lendico.plangenerator.service.RepaymentPlanService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
public class PlanController {

  public static final Logger LOGGER = LogManager.getLogger(PlanController.class);

  @Autowired RepaymentPlanService repaymentPlanService;

  @ApiOperation(
      nickname = "getPlans",
      value = "Get repayment schedule",
      response = RepaymentPlan.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Repayment plan created"),
        @ApiResponse(
            code = 400,
            message =
                "The request body is either malformed or contains unsupported parameter values."),
        @ApiResponse(code = 403, message = "Not authorized to call this service."),
        @ApiResponse(
            code = 500,
            message = "An unexpected error occurred while processing the request."),
        @ApiResponse(code = 502, message = "Application returned an unexpected error."),
        @ApiResponse(code = 504, message = "Application did not respond in a timely fashion.")
      })
  @PostMapping(produces = "application/json", consumes = "application/json")
  public ResponseEntity<RepaymentPlan> getPlans(
      @Valid @RequestBody PlanCriteria planCriteria, Principal principal) {
    LOGGER.debug("Request Criteria: {}", planCriteria);
    LOGGER.info(
        "User {} requested repayment plan for the duration of {}",
        principal.getName(),
        planCriteria.getDuration());
    return new ResponseEntity<>(repaymentPlanService.getRepaymentPlan(planCriteria), HttpStatus.OK);
  }
}
