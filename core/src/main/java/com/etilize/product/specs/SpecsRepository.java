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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;

/**
 * {@link Specs} repository implementation
 *
 * @author Faisal Feroz
 *
 */
@Repository
public class SpecsRepository {

    private static final String SQL = "SELECT p.productid, pl.languageid, TRIM( CONCAT_WS(' ', pv.value, pp.unitvalue)) as value " //
            + "FROM product p, productlanguage pl, productparameter pp, parametervalue pv " //
            + "WHERE p.productid in (:productIds) " //
            + "AND p.productid = pl.productid " //
            + "AND pl.productid = pp.productid " //
            + "AND pp.valueid = pv.valueid " //
            + "AND pl.languageid in (1,9,10,11) " //
            + "AND pp.languageid in (1,9,10,11) " //
            + "AND pl.productstatusid = 6 " //
            + "AND pp.parameterid = :parameterId " //
            + "AND pp.exceptionCodeId = 2 " //
            + "AND p.isactive = 1 " //
            + "AND pl.isactive = 1 " //
            + "AND pp.isactive = 1 " //
            + "AND pv.isactive =1";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * Returns Specs for the given productId for the given parameterId having the
     * published status in English language
     *
     * @param productIds
     * @param parameterId
     * @return
     */
    public List<Specs> findAllByProductIdAndParameterIdWithPublishedStatus(
            final List<Integer> productIds, final Integer parameterId) {
        final Map<String, Object> params = Maps.newHashMap();
        params.put("productIds", productIds);
        params.put("parameterId", parameterId);

        return jdbcTemplate.query(SQL, params, new ResultSetExtractor<List<Specs>>() {

            @Override
            public List<Specs> extractData(final ResultSet rs) throws SQLException {
                final Map<Integer, Specs> specs = Maps.newHashMap();
                while (rs.next()) {
                    final int productId = rs.getInt(1); // NOSONAR
                    final int languageId = rs.getInt(2); // NOSONAR
                    final String value = rs.getString(3); // NOSONAR

                    if (!specs.containsKey(productId)) {
                        specs.put(productId, new Specs(productId, languageId));
                    }
                    specs.get(productId).addValue(value);
                }
                return new ArrayList<>(specs.values());
            }
        });
    }
}
