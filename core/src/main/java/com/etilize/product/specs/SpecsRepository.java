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

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * {@link Specs} repository implementation
 *
 * @author Faisal Feroz
 *
 */
@Repository
public class SpecsRepository {

    private static final int BATCH_SIZE = 100;

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

    private static final String PRODUCTS_BY_DATE_RANGE_CATEGORYID_TEMPLATEID_SQL = "SELECT  " //
            + "p.productid,pp.parameterid,pp.exceptioncodeid, pp.setnumber, pv.value , pp.unitvalue " //
            + "FROM (SELECT  prod.productid,prod.categoryid " //
            + "FROM product prod " + "/*!IGNORE INDEX (categoryid) */ "
            + "INNER JOIN Productmarket prod_market "
            + "ON prod.productid = prod_market.productid "
            + "AND prod_market.marketid = 1 AND prod_market.isactive =1 "
            + "INNER JOIN Productstatushistory psh "
            + "/*!USE INDEX (productid,timestamp) */"
            + "ON prod.productid = psh.productid " + "AND psh.languageid = 1 "
            + "AND psh.productstatusid = 6 " + "AND psh.isactive = 1 "
            + "WHERE psh.timestamp between :fromDate " + "AND :toDate "
            + "AND prod.categoryID=:categoryId " + "group by prod.productid) p "
            + "INNER JOIN Productparameter pp "
            + "/*!USE index (productid,parameterid) */"
            + "ON  p.productId = pp.productId  AND p.categoryid = pp.categoryid AND pp.isactive =1 "
            + "INNER JOIN Parametervalue pv ON pp.valueId = pv.valueId "
            + "INNER JOIN Template t ON p.categoryId = t.categoryId  "
            + "AND t.templateid = :templateId AND t.isactive =1 "
            + "INNER JOIN Templateheader th "
            + "ON t.templateid = th.templateid and th.isactive =1 "
            + "INNER JOIN Header h ON th.headerid = h.headerid  and h.isactive =1 "
            + "INNER JOIN Templateattribute ta ON th.templateheaderid = ta.templateheaderid "
            + "AND ta.isactive =1 and ta.isdeprecated = 0 " + "INNER JOIN Attribute a "
            + "ON ta.attributeid = a.attributeid  and a.isactive =1 "
            + "INNER JOIN Atrparamdef atr "
            + "ON atr.atrparamdefid = ta.atrparamdefid AND a.attributeid = atr.attributeid "
            + "AND atr.isactive =1 and atr.languageid =1 "
            + "INNER JOIN Atrparamdefcomp atrc "
            + "ON atr.atrparamdefid = atrc.atrparamdefid and atrc.isactive =1  and atrc.parameterid > 0 "
            + "INNER JOIN Parameter param " + "ON atrc.parameterid = param.parameterid  "
            + "AND param.isactive = 1 AND  pp.parameterid = param.parameterid "
            + "INNER JOIN Categoryparameter cp "
            + "ON p.categoryid = cp.categoryid AND cp.parameterid = param.parameterid AND cp.isactive =1";

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
        final List<Specs> specs = Lists.newArrayList();
        final Map<String, Object> params = Maps.newHashMap();
        for (final List<Integer> pIds : Lists.partition(productIds, BATCH_SIZE)) {
            params.put("productIds", pIds);
            params.put("parameterId", parameterId);

            specs.addAll(jdbcTemplate.query(SQL, params,
                    new ResultSetExtractor<List<Specs>>() {

                        @Override
                        public List<Specs> extractData(final ResultSet rs)
                                throws SQLException {
                            final Map<Integer, Specs> specs = Maps.newHashMap();
                            while (rs.next()) {
                                final int productId = rs.getInt(1); // NOSONAR
                                final int languageId = rs.getInt(2); // NOSONAR
                                final String value = rs.getString(3); // NOSONAR

                                if (!specs.containsKey(productId)) {
                                    specs.put(productId,
                                            new Specs(productId, languageId));
                                }
                                specs.get(productId).addValue(value);
                            }
                            return new ArrayList<>(specs.values());
                        }
                    }));
        }
        return specs;
    }

    /**
     * Returns List of ProductItem w.r.t provided Date Range and TemplateId
     *
     * @param startDate
     * @param endDate
     * @param categoryId
     * @param templateId
     * @return
     */
    public List<ProductItem> findProductItemByDateRangeCategoryIdAndTemplateId(
            final String startDate, final String endDate, final Integer categoryId,
            final Integer templateId) { //
        final Map<String, Object> params = Maps.newHashMap();

        Logger.getLogger(SpecsRepository.class).info("This is query execute");

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
        params.put("categoryId", categoryId);

        return jdbcTemplate.query(PRODUCTS_BY_DATE_RANGE_CATEGORYID_TEMPLATEID_SQL,
                params, new ResultSetExtractor<List<ProductItem>>() {

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

                            final ExceptionCode exceptioncode = ExceptionCode.valueOf(
                                    exceptionCodeId);

                            if (!productMap.containsKey(productId)) {
                                productMap.put(productId, new ProductItem(productId));
                            }
                            productMap.get(productId).addParameter(new ProductParameter(
                                    parameterId, exceptioncode, setNumber, unit, value));

                        }
                        return new ArrayList<>(productMap.values());
                    }
                });
    }

    public List<ProductItem> findProductAndValues() { //
        final Map<String, Object> params = Maps.newHashMap();

        Logger.getLogger(SpecsRepository.class).info("This is query execute");
        final String fileName = "NoteBook_2020.csv";

        return jdbcTemplate.query(SQL_Param, params,
                new ResultSetExtractor<List<ProductItem>>() {

                    @Override
                    public List<ProductItem> extractData(final ResultSet rs)
                            throws SQLException {

                        Logger.getLogger(SpecsRepository.class).info("Query is Executed");

                        try {
                            FileWriter writer = new FileWriter(
                                    "/media/D-Data/inference_Data/server_and_array_with_paragraph_new/Prameter_only/"
                                            + fileName);
                            String prodId = "1";
                            int count = 0;
                            while (rs.next()) {
                                final String productId = rs.getString(1); // NOSONAR
                                final int parameterId = rs.getInt(2); // NOSONAR
                                final int exceptionCodeId = rs.getInt(3); // NOSONAR
                                final String value = rs.getString(4); // NOSONAR
                                final String unit = rs.getString(5); // NOSONAR

                                String parameterValue = null;
                                if (exceptionCodeId != 2) {
                                    parameterValue = parameterId + "|" + "_EX_"
                                            + exceptionCodeId;
                                    // System.out.println("Exception
                                    // code"+parameterValue);
                                }
                                if (unit != null && unit.equals("6666")) {
                                    // String value2 = value.replaceAll("\\<.*?\\>", "");
                                    if (!value.isEmpty()
                                            && !value.trim().startsWith("<a href")) {
                                        parameterValue = parameterId + "|" + value.trim();
                                        parameterValue = "\"" + parameterValue + "\"";
                                        count++;
                                    }
                                } else {
                                    if (unit != null && unit.equalsIgnoreCase("Month")
                                            && value.equals("12")) {
                                        parameterValue = parameterId + "|1 Year";
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("g")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(1000)) >= 0)) {
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(1000));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " kg";
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("mm")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(10)) >= 0)) {
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(10));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " cm";
                                        System.out.println(
                                                value + "mm to cm Convert Value:"
                                                        + parameterValue);
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("cm")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(100)) >= 0)) {
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(100));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " m";
                                        System.out.println(
                                                value + "cm to m Convert Value:"
                                                        + parameterValue);
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("oz")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(16)) >= 0)) {
                                        System.out.println("should change oz to pound productId["
                                                        + productId + "] parameterId["
                                                        + parameterId + "] value[" + value
                                                        + "] unit[" + unit + "]");
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(16));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " lb";
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("MB")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(1000)) >= 0)) {
                                        System.out.println(
                                                "should change MB to GB productId["
                                                        + productId + "] parameterId["
                                                        + parameterId + "] value[" + value
                                                        + "] unit[" + unit + "]");
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(1000));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " GB";
                                        count++;
                                    } else if (unit != null && unit.equalsIgnoreCase("GB")
                                            && (new BigDecimal(value).compareTo(
                                                    new BigDecimal(1000)) >= 0)) {
                                        System.out.println(
                                                "should change GB to TB productId["
                                                        + productId + "] parameterId["
                                                        + parameterId + "] value[" + value
                                                        + "] unit[" + unit + "]");
                                        BigDecimal value1 = new BigDecimal(value).divide(
                                                new BigDecimal(1000));
                                        parameterValue = parameterId + "|"
                                                + value1.setScale(2,
                                                        BigDecimal.ROUND_HALF_EVEN)
                                                + " TB";
                                        count++;
                                    } else {
                                        if (unit != null && unit.equals("\"")) {
                                            parameterValue = "\"" + parameterId + "|"
                                                    + (value + unit + '"' + '"');
                                            System.out.println("Convert Inch value Value:"
                                                    + parameterValue);
                                            count++;
                                        } else {
                                            if (value != null && !value.isEmpty()) {
                                                parameterValue = parameterId + "|" + ((unit != null
                                                    && !unit.isEmpty())? (value + " "+ unit) : value);
                                                System.out.println("Not Convert Value:"
                                                        + parameterValue);
                                                count++;
                                            }
                                        }
                                    }

                                }

                                if (!productId.equals(prodId)) {
                                    writer.append("\n");
                                    writer.append(productId);
                                    prodId = productId;
                                }
                                if (parameterValue != null) {
                                    writer.append("\t");
                                }
                                if (parameterValue != null) {
                                    writer.append(parameterValue.trim());
                                }
                            }
                            System.out.println("Total number of records[" + count + "]");
                            writer.close();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return new ArrayList<>(null);
                    }
                });
    }

    final static String ListproductIds = "1025542107,1033324245,1033324247,1033324249,1033324251,1033324252,1033324254,1033324255,1033324257,1033324263,1033324266,1033324267,1033324300,1033324301,1033324302,1033324304,1033324311,1033324313,1033324314,1033324316,1033324317,1033324319,1033324322,1033324323,1033324330,1033324333,1033324349,1033324352,1033324374,1033324472,1033324473,1033324474,1033324475,1033324477,1033324478,1035571263,1035571264,1035571265,1035571266,1035571267,1035571268,1035571269,1035571270,1035571271,1035571272,1035571273,1035571274,1035571323,1035571324,1035571325,1035571326,1037670511,1037670552,1037670564,1037670577,1037670579,1037670598,1038607521,1040650469,1040670519,1040670563,1040672921,1040748701";

    private static final String SQL_Param = "(SELECT p.productid,pp.parameterid,pp.exceptioncodeid, pv.value , pp.unitvalue"
            /*
             * +
             * " CASE WHEN exceptioncodeid != 2 then CONCAT(pp.parameterid,'|','_EX_',exceptioncodeid,(CASE WHEN value<>'' then '|' ELSE '' END),value) ELSE "
             * +
             * "CONCAT(pp.parameterid,'|',value,' ',IFNULL(unitvalue,'')) END AS Paramvalue "
             */
            + " FROM (SELECT  prod.productid,prod.categoryid" + "  FROM product prod "
            + "/*!IGNORE INDEX (categoryid) */ " + "INNER JOIN Productmarket prod_market "
            + "ON prod.productid = prod_market.productid "
            + " INNER JOIN Productstatushistory psh "
            + "/*!USE INDEX (productid,timestamp) */"
            + "ON prod.productid = psh.productid " + "    AND psh.productstatusid = 6 "
            + "AND psh.isactive = 1 " + "    AND prod.categoryID=10154"
            + " AND prod.productid in ("+ListproductIds+")"
            + " group by prod.productid) p " + " INNER JOIN Productparameter pp "
            + " /*!USE index (productid,parameterid) */"
            + " ON  p.productId = pp.productId  AND p.categoryid = pp.categoryid AND pp.isactive =1 "
            + " INNER JOIN Parametervalue pv ON pp.valueId = pv.valueId "
            + " INNER JOIN Template t ON p.categoryId = t.categoryId  "
            + " AND t.templateid = 11643 AND t.isactive = 1 "
            + " INNER JOIN Templateheader th "
            + " ON t.templateid = th.templateid and th.isactive =1 "
            + " INNER JOIN Header h ON th.headerid = h.headerid  and h.isactive =1 "
            + " INNER JOIN Templateattribute ta ON th.templateheaderid = ta.templateheaderid "
            + " AND ta.isactive =1 and ta.isdeprecated = 0 " + " INNER JOIN Attribute a "
            + " ON ta.attributeid = a.attributeid  and a.isactive =1 "
            + " INNER JOIN Atrparamdef atr "
            + " ON atr.atrparamdefid = ta.atrparamdefid AND a.attributeid = atr.attributeid  "
            + " AND atr.isactive =1 and atr.languageid =1 "
            + " INNER JOIN Atrparamdefcomp atrc "
            + " ON atr.atrparamdefid = atrc.atrparamdefid and atrc.isactive =1  and atrc.parameterid > 0 "
            + " INNER JOIN Parameter param "
            + " ON atrc.parameterid = param.parameterid  "
            + " AND param.isactive = 1 AND  pp.parameterid = param.parameterid "
            + " INNER JOIN Categoryparameter cp "
            + " ON p.categoryid = cp.categoryid AND cp.parameterid = param.parameterid AND cp.isactive =1)"

    + "UNION"
            + " (SELECT p.productid,pp.parameterid,pp.exceptioncodeid, pp.value, 6666 as unitvalue "
            + " FROM (SELECT  prod.productid,prod.categoryid" + " FROM product prod "
            + " /*!IGNORE INDEX (categoryid) */ "
            + " INNER JOIN Productmarket prod_market "
            + " ON prod.productid = prod_market.productid "
            + " INNER JOIN Productstatushistory psh"
            + " /*!USE INDEX (productid,timestamp) */"
            + " ON prod.productid = psh.productid " + " AND psh.productstatusid = 6 "
            + " AND psh.isactive = 1 " + " AND prod.categoryID=10154"
            + " AND prod.productid in (" + ListproductIds + ")"
            + " group by prod.productid) p "
            + " INNER JOIN paragraphproductparameter pp "
            + " /*!USE index (productid,parameterid) */"
            + " ON  p.productId = pp.productId  AND p.categoryid = pp.categoryid "
            + " and pp.value!='' and pp.value is not null "
            + " and pp.languageid in (1,9,10,11) "
            + " INNER JOIN Template t ON p.categoryId = t.categoryId  "
            + " AND t.templateid = 11643 AND t.isactive = 1 "
            + " INNER JOIN Templateheader th "
            + " ON t.templateid = th.templateid and th.isactive =1 "
            + " INNER JOIN Header h ON th.headerid = h.headerid  and h.isactive =1 "
            + " INNER JOIN Templateattribute ta ON th.templateheaderid = ta.templateheaderid "
            + " AND ta.isactive =1 and ta.isdeprecated = 0 " + " INNER JOIN Attribute a "
            + " ON ta.attributeid = a.attributeid  and a.isactive =1 "
            + " INNER JOIN Atrparamdef atr "
            + " ON atr.atrparamdefid = ta.atrparamdefid AND a.attributeid = atr.attributeid"
            + " AND atr.isactive =1 and atr.languageid =1 "
            + " INNER JOIN Atrparamdefcomp atrc "
            + " ON atr.atrparamdefid = atrc.atrparamdefid and atrc.isactive =1  and atrc.parameterid > 0 "
            + " INNER JOIN Parameter param " + " ON atrc.parameterid = param.parameterid "
            + " AND param.isactive = 1 AND  pp.parameterid = param.parameterid "
            + " INNER JOIN Categoryparameter cp "
            + " ON p.categoryid = cp.categoryid AND cp.parameterid = param.parameterid AND cp.isactive =1)";
}
