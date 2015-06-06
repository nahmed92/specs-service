/*
 * #region
 * specs-service-client
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

package com.etilize.product.specs.client;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.springframework.web.client.AsyncRestTemplate;

public class SpecsServiceClientTest {

    private final String BASE_URL = "http://localhost:7000";

    @Test
    public void shouldAutoCreateRestTemplateIfNotSet() throws Exception {
        final SpecsServiceClient client = new SpecsServiceClient(BASE_URL);
        client.afterPropertiesSet();
        assertThat(client.getRestTemplate(), is(notNullValue()));
    }

    @Test
    public void shouldnotAutoCreateRestTemplateIfNotSet() throws Exception {
        final SpecsServiceClient client = new SpecsServiceClient(BASE_URL);
        final AsyncRestTemplate restTemplate = new AsyncRestTemplate();
        client.setRestTemplate(restTemplate);
        client.afterPropertiesSet();
        assertThat(restTemplate, is(client.getRestTemplate()));
    }
}
