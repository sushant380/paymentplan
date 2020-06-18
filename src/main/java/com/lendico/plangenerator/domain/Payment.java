package com.lendico.plangenerator.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Period Payment Details")
public class Payment {
  @ApiModelProperty(value = "Due amount of the month", example = "2300")
  private BigDecimal borrowerPaymentAmount;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  @ApiModelProperty(value = "Payment due date", example = "2020:06:01T10:00:00Z")
  private Date date;

  @ApiModelProperty(value = "Last Outstanding principal", example = "2300")
  private BigDecimal initialOutstandingPrincipal;

  @ApiModelProperty(value = "Interest of this month", example = "20.01")
  private BigDecimal interest;

  @ApiModelProperty(value = "Principal amount", example = "2300")
  private BigDecimal principal;

  @ApiModelProperty(value = "Remaining outstanding amount", example = "2300")
  private BigDecimal remainingOutstandingPrincipal;
}
