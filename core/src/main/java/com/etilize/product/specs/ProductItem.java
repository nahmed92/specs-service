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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * <p>
 * ProductItem holds List of product parameter with respect to productId
 * <p>
 *
 * @author Nasir Ahmed
 */
public class ProductItem {

    private final Integer productId;

    private final List<ProductParameter> productParameters = new ArrayList<>();

    /**
     * @param productId
     */
    public ProductItem(final Integer productId) {
        this.productId = productId;
    }

    /**
     * @return the productId
     */
    public Integer getProductId() {
        return productId;
    }

    /**
     * @return the productItems
     */
    public List<ProductParameter> getProductParameters() {
        return productParameters;
    }

    /**
     * @return the productItems
     */
    public void addParameter(final ProductParameter productParameters) {
        this.productParameters.add(productParameters);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this) //
        .append("ProductId", getProductId()) //
        .append("Parameters Count", getProductParameters().size()) //
        .toString();
    }
}
