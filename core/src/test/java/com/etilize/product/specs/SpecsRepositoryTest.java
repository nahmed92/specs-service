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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.etilize.product.specs.test.base.AbstractIntegrationTest;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.Lists;

@DatabaseSetup(value = "specs.xml", type = DatabaseOperation.CLEAN_INSERT)
@DatabaseTearDown(value = "/reset.xml", type = DatabaseOperation.DELETE_ALL)
public class SpecsRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private SpecsRepository repo;

    @Test
    public void shouldFindAllByProductIdsAndParameterIdWithPublishedStatus() {
        final List<Integer> productIds = Lists.newArrayList(1, 2, 3);
        final Integer parameterId = 111;
        final List<Specs> specs = repo.findAllByProductIdAndParameterIdWithPublishedStatus(
                productIds, parameterId);

        assertThat(specs, is(notNullValue()));
        assertThat(specs, hasSize(2));
        for (final Specs spec : specs) {
            assertThat(spec.getProductId(), anyOf(is(1), is(3)));
            assertThat(spec.getLanguageId(), is(1));
            assertThat(spec.getValues(),
                    anyOf(hasItems("Value 1 unit1", "Value 2"), hasItem("Value 3 unit3")));
        }
    }

}
