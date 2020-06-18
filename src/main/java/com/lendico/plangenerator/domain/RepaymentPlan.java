package com.lendico.plangenerator.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Repayment Plan")
public class RepaymentPlan {
  @ApiModelProperty(name = "borrowerPayments", value = "List of payments")
  private List<Payment> borrowerPayments;

  @ApiModelProperty(value = "total payments", name = "total")
  private Long total;
}
