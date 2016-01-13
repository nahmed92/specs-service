/*
 * #region
 * core
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

public enum ExceptionCode {
    NONE(1), //
    INCOMPLETE(2), //
    COMPLETE(3), //
    DATA_NOT_AVAILIBLE(4), //
    NOT_APPLICABLE(5), //
    CONFLICT(6), //
    NOT_INSTALLED(7), //
    NO(8), //
    UPGRADEABLE(9), //
    STATUS_VOID(10), //
    YES(11), //
    OPTIONAL(12), //
    UNKNOWN(13);

    private int code;

    ExceptionCode(final int code) {
        this.code = code;
    }

    public static ExceptionCode valueOf(final int code) {
        for (final ExceptionCode exceptionCode : ExceptionCode.values()) {
            if (exceptionCode.code == code) {
                return exceptionCode;
            }
        }
        throw new IllegalArgumentException("No Exception Code found for Code[" + code
                + "]");
    }
}
