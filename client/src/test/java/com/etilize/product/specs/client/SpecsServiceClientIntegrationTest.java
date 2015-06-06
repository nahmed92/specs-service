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

import static com.etilize.product.specs.client.SpecsServiceClient.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.ResourceUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.damnhandy.uri.template.UriTemplate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class SpecsServiceClientIntegrationTest {

    private static final String BASE_URL = "http://localhost:7000";

    private SpecsServiceClient client;

    private MockRestServiceServer mockServer;

    private boolean isInitialized = false;

    @Before
    public void setup() {
        client = new SpecsServiceClient(BASE_URL);
        client.afterPropertiesSet();

        mockServer = MockRestServiceServer.createServer(client.getRestTemplate());

        if (!isInitialized) {
            configureRestTemplate(client.getRestTemplate());
            isInitialized = true;
        }
    }

    @Test
    public void shouldFindByCategoryIdAndAttributeId() throws Exception {
        final List<Integer> productIds = Lists.newArrayList(1, 2, 3);
        final Integer parameterId = 11;
        final Map<String, Object> vars = new HashMap<>();
        vars.put("productIds", productIds);
        vars.put("parameterId", parameterId);
        final String url = UriTemplate.fromTemplate(BASE_URL + SEARCH_URL).expand(vars);
        final String body = FileUtils.readFileToString(ResourceUtils.getFile(appendClasspath("findAllByProductIdsAndParameterIdWithPublishedStatus.json")));

        mockServer.expect(requestTo(url)) //
        .andExpect(method(HttpMethod.GET)) //
        .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        final ListenableFuture<ResponseEntity<List<Specs>>> future = client.findAllByProductIdsAndParameterIdWithPublishedStatus(
                productIds, parameterId);
        assertThat(future, is(notNullValue()));
        assertThat(future.get(), is(notNullValue()));
        final List<Specs> specs = future.get().getBody();
        assertThat(specs, hasSize(2));
        for (final Specs spec : specs) {
            assertThat(spec.getProductId(), anyOf(is(1), is(3)));
            assertThat(spec.getValues(), hasSize(1));
            assertThat(spec.getValues(), anyOf(hasItem("256 MB"), hasItem("1 GB")));
        }
    }

    private String appendClasspath(final String filename) {
        return String.format("classpath:%s/%s",
                this.getClass().getPackage().getName().replace('.', '/'), filename);
    }

    private void configureRestTemplate(final AsyncRestTemplate restTemplate) {
        for (final HttpMessageConverter<?> converter : restTemplate.getMessageConverters()) {
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                final ObjectMapper objectMapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                objectMapper.registerModule(new Jackson2HalModule());
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false);
                objectMapper.getFactory().enable(JsonParser.Feature.ALLOW_COMMENTS);

            }
        }
    }
}
