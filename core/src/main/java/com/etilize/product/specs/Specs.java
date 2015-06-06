/*
 * #region
 * specs-service-core
 * %%
 * Copyright (C) 2013 - 2015 Etilize
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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Domain Object for Specs
 *
 * @author Faisal Feroz
 *
 */
public class Specs {

    private final Integer productId;

    private final Integer languageId;

    private final Set<String> values;

    /**
     * Constructor for specs object
     *
     * @param productId
     * @param languageId
     */
    public Specs(final Integer productId, final Integer languageId) {
        this(productId, languageId, new HashSet<String>());
    }

    /**
     * Constructor for specs object with values
     *
     * @param productId
     * @param languageId
     * @param values
     */
    public Specs(final Integer productId, final Integer languageId,
            final Set<String> values) {
        this.productId = productId;
        this.languageId = languageId;
        this.values = values;
    }

    public Integer getProductId() {
        return productId;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public Set<String> getValues() {
        return values;
    }

    public void addValue(final String value) {
        this.values.add(value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
        .append("ProductId", getProductId()) //
        .append("LanguageId", getLanguageId()) //
        .append("Values", getValues()) //
        .toString();
    }

}
