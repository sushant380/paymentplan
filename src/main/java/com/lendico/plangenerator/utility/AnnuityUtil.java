package com.lendico.plangenerator.utility;

import com.lendico.plangenerator.exception.DataException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Annuity Singleton class to generate Annuity amount based on general formula pmt= (r *
 * p)/(1-(1-r)^-n).
 */
public class AnnuityUtil {
  private static AnnuityUtil annuityUtil;

  public static final Logger LOGGER = LogManager.getLogger(AnnuityUtil.class);

  private AnnuityUtil() {}

  /**
   * Get Annuity amount.
   *
   * @param duration Period for loan repayment
   * @param nominalRate Nominal Rate in percentage
   * @param principalAmount Principal amount
   * @return Annuity amount to be paid over the period mentioned
   */
  public static BigDecimal getAnnuityAmount(
      Integer duration, BigDecimal nominalRate, BigDecimal principalAmount) {
    LOGGER.trace(
        "Calculate Annuity amount on {} with interest rate of {} for duration {}",
        principalAmount,
        nominalRate,
        duration);
    if (annuityUtil == null) {
      LOGGER.debug("Creating an instance of AnnuityUtil");
      annuityUtil = new AnnuityUtil();
    }
    // Calculate effective interest rate for each interval
    BigDecimal effectiveRate = annuityUtil.calculateEffectiveRate(nominalRate, duration);
    LOGGER.trace("Effective interest rate: {} ", effectiveRate);
    // Get annuity amount for each interval
    return annuityUtil.calculatePaymentAmount(duration, effectiveRate, principalAmount);
  }

  /**
   * Calculate Annuity payment based on effective interest rate and principal amount borrowed for
   * give duration.
   *
   * @param duration Period for loan repayment
   * @param effectiveRate effective monthly interest rate
   * @param principalAmount Principal amount
   * @return Annuity amount calculated for each month.
   */
  private BigDecimal calculatePaymentAmount(
      Integer duration, BigDecimal effectiveRate, BigDecimal principalAmount) {
    if (duration == null || effectiveRate == null || principalAmount == null) {
      LOGGER.error("It will throw an exception because one the input is invalid");
      LOGGER.debug("duration:{}", duration);
      LOGGER.debug("effectiveRate:{}", effectiveRate);
      LOGGER.debug("principalAmount:{}", principalAmount);
      throw new DataException(
          "These fields cannot be null for calculation. Duration("
              + duration
              + "), effectiveRate("
              + effectiveRate
              + ") principalAmount("
              + principalAmount);
    }
    MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
    return effectiveRate
        .multiply(principalAmount, mc)
        .divide(
            BigDecimal.ONE.subtract(BigDecimal.ONE.add(effectiveRate, mc).pow(-duration, mc)), mc)
        .setScale(2, RoundingMode.UP);
  }

  /**
   * Calculate effective interest rate for each month based on yearly rate. Formula : (r/100)/12
   * where r is nominal rate. 12 months in a year
   *
   * @param nominalRate Nominal Rate in percentage
   * @param duration Period for loan repayment
   * @return effective interest rate for each month
   */
  private BigDecimal calculateEffectiveRate(BigDecimal nominalRate, Integer duration) {
    if (duration == null || nominalRate == null) {
      LOGGER.error("It will throw an exception because one the input is invalid");
      LOGGER.debug("duration:{}", duration);
      LOGGER.debug("nominalRate:{}", nominalRate);
      throw new DataException(
          "These fields cannot be null for calculation. Duration("
              + duration
              + "), nominalRate("
              + nominalRate
              + ")");
    }
    MathContext mc = new MathContext(18, RoundingMode.HALF_UP);
    return nominalRate.divide(BigDecimal.valueOf(100), mc).divide(BigDecimal.valueOf(12), mc);
  }
}
