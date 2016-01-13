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

import static com.etilize.commons.test.web.servlet.result.LinkResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;

import com.etilize.product.specs.test.base.AbstractIntegrationTest;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;

@DatabaseSetup(value = "specs.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = "/reset.xml", type = DatabaseOperation.DELETE_ALL)
public class SpecsSearchRestIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void shouldListSearches() throws Exception {
        mockMvc.perform(get("/specs/search")) //
        .andExpect(status().isOk()) //
        .andExpect(
                links().withRelIsPresent(
                        SpecsLinks.REL_FIND_ALL_BY_PRODUCT_IDS_AND_PARAMETER_ID_WITH_PUBLISHED_STATUS));
    }

    @Test
    public void shouldFindAllByProductIdAndParameterIdWithPublishedStatus()
            throws Exception {
        mockMvc.perform(
                get("/specs/search/findAllByProductIdsAndParameterIdWithPublishedStatus") //
                .param("productIds", "1,2,3") //
                .param("parameterId", "111")) //
        .andExpect(status().isOk()) //
        .andExpect(jsonPath("$.[*]", hasSize(2))) //
        ;
    }

    @Test
    public void shouldReturnEmptyResultWhenNothingFound() throws Exception {
        mockMvc.perform(
                get("/specs/search/findAllByProductIdsAndParameterIdWithPublishedStatus") //
                .param("productIds", "1,2,3") //
                .param("parameterId", "0")) //
        .andExpect(status().isOk()) //
        .andExpect(jsonPath("$.[*]", hasSize(0))) //
        ;
    }

    @Test
    public void shouldReturnBadRequestWhenProductIdsOrParameterIdNotProvided()
            throws Exception {
        mockMvc.perform(
                get("/specs/search/findAllByProductIdsAndParameterIdWithPublishedStatus") //
                .param("parameterId", "111")) //
        .andExpect(status().isBadRequest());

        mockMvc.perform(
                get("/specs/search/findAllByProductIdsAndParameterIdWithPublishedStatus") //
                .param("productIds", "1")) //
        .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFindAllProductIdByDateRangeAndTemplateId() throws Exception {
        mockMvc.perform(
                get("/specs/search/findProductItemByDateRangeCategoryIdAndTemplateId") //
                .param("startDate", "2015-08-03") //
                .param("endDate", "2015-12-02") //
                .param("categoryId", "1") //
                .param("templateId", "1")) //
        .andExpect(status().isOk()) //
        .andExpect(jsonPath("$.[*]", hasSize(2))) //
        .andExpect(jsonPath("$.[0].productId", is(1))) //
        .andExpect(jsonPath("$.[0].productParameters", hasSize(4))) //
        .andExpect(jsonPath("$.[1].productId", is(2))) //
        .andExpect(jsonPath("$.[1].productParameters", hasSize(1))) //
        .andExpect(jsonPath("$.[1].productParameters.parameterId[0]", is(115)));

    }
}
