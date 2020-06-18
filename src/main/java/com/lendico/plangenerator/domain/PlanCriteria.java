package com.lendico.plangenerator.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import org.hibernate.validator.constraints.Range;

@Value
@Getter
@Builder
@ApiModel(value = "Plan criteria")
public class PlanCriteria {
  @NotNull(message = "Duration is mandatory. e.g. duration:12")
  @Range(min = 1, message = "Duration cannot be lesser than 1")
  @ApiModelProperty(
      name = "duration",
      value = "Duration of loan",
      required = true,
      example = "24",
      dataType = "int")
  Integer duration;

  @NotNull(message = "Nominal interest rate is required e.g. nominalRate:5")
  @DecimalMin(value = "0.0", message = "Nominal Rate cannot be lesser than 0 or equal to zero")
  @ApiModelProperty(
      name = "nominalRate",
      value = "Nominal interest rate per year",
      required = true,
      example = "5",
      dataType = "int")
  BigDecimal nominalRate;

  @NotNull(message = "Please provide loan principal amount e.g. loanAmount:5000")
  @DecimalMin(value = "0.0", message = "Loan Amount cannot be lesser than 0 or equal to zero")
  @ApiModelProperty(
      name = "loanAmount",
      value = "Borrowed loan amount",
      required = true,
      example = "5000",
      dataType = "int")
  BigDecimal loanAmount;

  @NotNull(message = "Please provide start date of the loan e.g. startDate:2020:06:01T10:00:00Z")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  @ApiModelProperty(
      name = "statDate",
      value = "Loan start date",
      required = true,
      dataType = "date")
  Date statDate;
}
