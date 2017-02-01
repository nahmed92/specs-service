/*
 * #region
 * specs-service-core
 * %%
 * Copyright (C) 2013 - 2016 Etilize
 * %%
 * NOTICE: All information contained herein is, and remains the property of ETILIZE.
 * The intellectual and technical concepts contained herein are proprietary to
 * ETILIZE and may be covered by U.S. and Foreign Patents, patents in process, and
 * are protected by trade secret or copyright law. Dissemination of this information
 * or reproduction of this material is strictly forbidden unless prior written
 * permission is obtained from ETILIZE. Access to the source code contained herein
 * is hereby forbidden to anyone except current ETILIZE employees, managers or
 * contractors who have executed Confidentiality and Non-disclosure agreements
 * explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication
 * or disclosure of this source code, which includes information that is confidential
 * and/or proprietary, and is a trade secret, of ETILIZE. ANY REPRODUCTION, MODIFICATION,
 * DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY OF OR THROUGH USE OF THIS
 * SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ETILIZE IS STRICTLY PROHIBITED,
 * AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES. THE RECEIPT
 * OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT CONVEY OR
 * IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS CONTENTS, OR TO
 * MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 * #endregion
 */

package com.etilize.product.specs;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * ProductParameter contain Parameter like parameterId, SetNumber, exceptionCode , unit
 * and value of Parameter
 * <p>
 *
 * @author Nasir Ahmed
 **/
public class ProductParameter {

    private final Integer parameterId;

    private final ExceptionCode exceptionCode;

    private final Integer setNumber;

    private final String unit;

    private final String value;

    /**
     * @param parameterId
     * @param exceptionCode
     * @param setNumber
     * @param unit
     * @param value
     */
    public ProductParameter(final Integer parameterId, final ExceptionCode exceptionCode,
            final Integer setNumber, final String unit, final String value) {
        this.parameterId = parameterId;
        this.exceptionCode = exceptionCode;
        this.setNumber = setNumber;
        this.unit = unit;
        this.value = value;
    }

    /**
     * @return the parameterId
     */
    public Integer getParameterId() {
        return parameterId;
    }

    /**
     * @return the exceptionCodeId
     */
    public ExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    /**
     * @return the setNumber
     */
    public Integer getSetNumber() {
        return setNumber;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the value
     */
    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
        .append("ParameterId", getParameterId()) //
        .append("SetNumber", getSetNumber()) //
        .append("ExceptionCode", getExceptionCode()) //
        .append("Unit", getUnit()) //
        .append("Value", getValue()) //
        .toString();

    }
}
