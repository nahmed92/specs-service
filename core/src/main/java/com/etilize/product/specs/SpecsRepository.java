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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static final String SQL = "SELECT p.productid, pl.languageid, "//
            + "TRIM( CONCAT_WS(' ', pv.value, pp.unitvalue)) as value " //
            + "FROM product p, productlanguage pl, " //
            + "productparameter pp, parametervalue pv " //
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
            + "AND pv.isactive = 1";

    private static final String PRODUCTS_BY_DATE_RANGE_AND_TEMPLATEID_SQL = "SELECT "
            + "p.productid,pp.parameterid," //
            + "pp.exceptionCodeId, pp.setnumber, pv.value , pp.unitvalue " //
            + "FROM Product p " //
            + "INNER JOIN Productparameter pp ON  p.productId = pp.productId " //
            + "INNER JOIN Parametervalue pv ON pp.valueId = pv.valueId " //
            + "INNER JOIN Category c ON p.categoryId = c.categoryId " //
            + "INNER JOIN Template t ON c.categoryId = t.categoryId " //
            + "INNER JOIN Templateheader th ON t.templateid = th.templateid " //
            + "INNER JOIN Header h ON th.headerid = h.headerid " //
            + "INNER JOIN Templateattribute ta ON th.templateheaderid = ta.templateheaderid " //
            + "INNER JOIN Attribute a ON ta.attributeid = a.attributeid " //
            + "INNER JOIN Atrparamdef atr ON (a.attributeid = atr.attributeid " //
            + "AND atr.atrparamdefid = ta.atrparamdefid) " //
            + "INNER JOIN Atrparamdefcomp atrc ON atr.atrparamdefid = atrc.atrparamdefid " //
            + "INNER JOIN Parameter param ON atrc.parameterid = param.parameterid " //
            + "INNER JOIN Categoryparameter cp ON (c.categoryid = cp.categoryid "
            + "and  cp.parameterid = param.parameterid) " //
            + "INNER JOIN ( " //
            + "SELECT prod.productid, " //
            + "psh.timestamp timestamp FROM product prod " //
            + "INNER JOIN Productmarket prod_market " //
            + "ON prod.productid = prod_market.productid " //
            + "INNER JOIN Productstatushistory psh " //
            + "ON prod.productid = psh.productid " //
            + "WHERE psh.languageid = 1 " // 1 English
            + "AND prod_market.marketid = 1 " // 1 US
            + "AND psh.productstatusid = 6 " // 6 publish
            + "AND psh.isactive = 1 " //
            + "GROUP by prod.productid,psh.timestamp " //
            + "HAVING psh.timestamp between :fromDate " //
            + "AND :toDate ) pId " //
            + "ON p.productid = pId.productid " //
            + "WHERE  t.templateId = :templateId " //
            + "AND p.isactive = 1 " //
            + "AND pp.isactive = 1 " //
            + "AND pv.isactive = 1 ";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private static final String START_DATE_TIMESTAMP = "00:00:00";

    private static final String END_DATE_TIMESTAMP = "23:59:59";

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

    /**
     * Returns List of ProductItem w.r.t provided Date Range and TemplateId
     *
     * @param Date startDate
     * @param Date endDate
     * @param TemplateId
     * @return List<ProductItem>
     */
    public List<ProductItem> findProductItemByDateRangeAndTemplateId(
            final String startDate, final String endDate, final Integer templateId) { //
        final Map<String, Object> params = Maps.newHashMap();

        // Format Date with timestamp
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fromDate = null;
        Date toDate = null;
        try {
            fromDate = format.parse(startDate + " " + START_DATE_TIMESTAMP);
            toDate = format.parse(endDate + " " + END_DATE_TIMESTAMP);
        } catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        }

        params.put("fromDate", fromDate);
        params.put("toDate", toDate);
        params.put("templateId", templateId);

        return jdbcTemplate.query(PRODUCTS_BY_DATE_RANGE_AND_TEMPLATEID_SQL, params,
                new ResultSetExtractor<List<ProductItem>>() {

                    @Override
                    public List<ProductItem> extractData(final ResultSet rs)
                            throws SQLException {
                        final Map<Integer, ProductItem> productMap = Maps.newHashMap();

                        while (rs.next()) {
                            final int productId = rs.getInt(1); // NOSONAR
                            final int parameterId = rs.getInt(2); // NOSONAR
                            final int exceptionCodeId = rs.getInt(3); // NOSONAR
                            final int setNumber = rs.getInt(4); // NOSONAR
                            final String value = rs.getString(5); // NOSONAR
                            final String unit = rs.getString(6); // NOSONAR

                            final ExceptionCode exceptioncode = ExceptionCode.valueOf(exceptionCodeId);

                            if (!productMap.containsKey(productId)) {
                                productMap.put(productId, new ProductItem(productId));
                            }
                            productMap.get(productId).addParameter(
                                    new ProductParameter(parameterId, exceptioncode,
                                            setNumber, unit, value));

                        }
                        return new ArrayList<>(productMap.values());
                    }
                });
    }

}
