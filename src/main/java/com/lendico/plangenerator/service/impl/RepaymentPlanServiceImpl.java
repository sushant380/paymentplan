package com.lendico.plangenerator.service.impl;

import com.lendico.plangenerator.domain.Payment;
import com.lendico.plangenerator.domain.PlanCriteria;
import com.lendico.plangenerator.domain.RepaymentPlan;
import com.lendico.plangenerator.service.RepaymentPlanService;
import com.lendico.plangenerator.utility.AnnuityUtil;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Repayment service implementation to calculate repayment amount based on different formulas and
 * figures.
 */
@Service
public class RepaymentPlanServiceImpl implements RepaymentPlanService {

  public static final Logger LOGGER = LogManager.getLogger(RepaymentPlanServiceImpl.class);
  private static final int DAYS_IN_MONTH = 30;
  private static final int DAYS_IN_YEAR = DAYS_IN_MONTH * 12;

  /**
   * Executes multiple calculation to get repayment plan.
   *
   * @param criteria input criteria
   * @return repayment plan for provided duration.
   */
  @Override
  public RepaymentPlan getRepaymentPlan(PlanCriteria criteria) {
    // Calculate annuity amount at the start of the loan.
    BigDecimal pmtAmount =
        AnnuityUtil.getAnnuityAmount(
            criteria.getDuration(), criteria.getNominalRate(), criteria.getLoanAmount());
    LOGGER.trace("Annuity Amount:{}", pmtAmount);
    // Calculate monthly payment details based on annuity amount.
    return calculateMonthlyData(pmtAmount, criteria);
  }

  /**
   * Iterate through each month to calculate various changing payment figures.
   *
   * @param pmtAmount Annuity Amount
   * @param criteria other loan details
   * @return repayment plan for each month
   */
  private RepaymentPlan calculateMonthlyData(BigDecimal pmtAmount, PlanCriteria criteria) {
    BigDecimal currentInitialOutstanding = criteria.getLoanAmount();
    Date currentDate = criteria.getStatDate();
    List<Payment> paymentList = new ArrayList<>();
    MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
    LOGGER.info("Generating repayment plan ");
    LOGGER.info("Monthly payment :{} ", pmtAmount);
    LOGGER.info("Monthly Interest rate :{} ", criteria.getNominalRate());
    LOGGER.info("Principal :{} ", criteria.getLoanAmount());
    LOGGER.info("Duration :{} ", criteria.getDuration());
    for (int interval = 0; interval < criteria.getDuration(); interval++) {
      int actualInterval = (interval + 1);
      LOGGER.trace(
          "Current outstanding amount for interval {}:{}",
          actualInterval,
          currentInitialOutstanding);

      // Calculate current interest amount based on current outstanding principal.
      BigDecimal interest = calculateInterest(criteria.getNominalRate(), currentInitialOutstanding);
      LOGGER.trace("Interest for interval {}:{}", actualInterval, interest);

      // Calculate principal amount deducted for this month.
      BigDecimal principal = calculatePrincipal(pmtAmount, interest);
      LOGGER.trace("Principal Amount for interval {}:{}", actualInterval, principal);

      // In case current outstanding amount is greater than annuity amount then change annuity
      // amount to current outstanding amount to avoid extra pay borrower
      // also principal becomes outstanding amount
      if (currentInitialOutstanding.compareTo(pmtAmount) <= 0) {
        pmtAmount = currentInitialOutstanding;
        principal = currentInitialOutstanding;
        LOGGER.trace(
            "Principal & pmt Amount for interval {} set back to current outstanding amount:",
            actualInterval,
            currentInitialOutstanding);
      }

      BigDecimal remainingOutstandingPrincipal =
          currentInitialOutstanding.subtract(principal.setScale(2, RoundingMode.HALF_UP));
      LOGGER.trace(
          "Remaining outstanding Amount for interval {}:{}",
          actualInterval,
          remainingOutstandingPrincipal);

      // Building Payment model using lombok builder.
      Payment payment =
          Payment.builder()
              .borrowerPaymentAmount(pmtAmount)
              .interest(interest)
              .principal(principal)
              .initialOutstandingPrincipal(currentInitialOutstanding)
              .remainingOutstandingPrincipal(remainingOutstandingPrincipal)
              .date(currentDate)
              .build();
      paymentList.add(payment);

      // Calculate future date for the payments considering 30 days a month.
      currentDate = calculatePaymentNextDate(currentDate);
      LOGGER.trace("Next date for payment is set to:{}", currentDate);

      // Deduct principal amount paid from outstanding principal.
      currentInitialOutstanding =
          currentInitialOutstanding.subtract(principal, mc).setScale(2, RoundingMode.HALF_UP);
    }
    LOGGER.trace("Number of payments:", paymentList.size());
    LOGGER.info("Plan generated for the requested duration:{}", criteria.getDuration());
    return RepaymentPlan.builder()
        .borrowerPayments(paymentList)
        .total((long) paymentList.size())
        .build();
  }

  /**
   * Calculate Interest amount per month based on outstanding principal amount Assumptions: Month=30
   * Days, Year=360 Days. formula: ( ( (rate/100) * 30) / 360 )
   *
   * @param rate Nominal rate
   * @param initialOutstanding outstanding amount
   * @return Interest amount
   */
  private BigDecimal calculateInterest(BigDecimal rate, BigDecimal initialOutstanding) {
    MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
    LOGGER.debug(
        "Calculate interest on Initial outstanding amount :{} Nominal rate:{}",
        initialOutstanding,
        rate);
    return rate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(DAYS_IN_MONTH), mc)
        .multiply(initialOutstanding, mc)
        .divide(BigDecimal.valueOf(DAYS_IN_YEAR), 4, RoundingMode.HALF_UP)
        .setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculate principal amount to be paid for the month based on interest rate calculate for the
   * month.
   *
   * @param annuity Annuity amount.
   * @param interest monthly interest amount.
   * @return Principal amount to be paid for the month.
   */
  private BigDecimal calculatePrincipal(BigDecimal annuity, BigDecimal interest) {
    LOGGER.debug("calculate principal on annuity :{} rate:{}", annuity, interest);
    MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
    return annuity.subtract(interest, mc).setScale(2, RoundingMode.HALF_UP);
  }

  /**
   * Calculate next payment due date. Assumptions: Days in month=30 days.
   *
   * @param currentDate current due date.
   * @return upcoming due date.
   */
  private Date calculatePaymentNextDate(Date currentDate) {
    LOGGER.debug("calculate next payment due date based on {}", currentDate);
    return Date.from(currentDate.toInstant().plus(Period.ofDays(DAYS_IN_MONTH)));
  }
}
