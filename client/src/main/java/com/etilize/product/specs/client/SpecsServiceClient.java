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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.damnhandy.uri.template.MalformedUriTemplateException;
import com.damnhandy.uri.template.UriTemplate;
import com.damnhandy.uri.template.VariableExpansionException;

public class SpecsServiceClient implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final String SPECS_PATH = "/specs";

    static final String SEARCH_URL = SPECS_PATH
            + "/search/findAllByProductIdsAndParameterIdWithPublishedStatus{?productIds*,parameterId}";

    /**
     * root url of the Sampling Service service
     */
    private final String baseUrl;

    private AsyncRestTemplate restTemplate;

    public SpecsServiceClient(final String baseUrl) {
        Assert.hasText(baseUrl);
        this.baseUrl = baseUrl;
    }

    /**
     * Finds the data extracted for the given parameterId in given productIds whos status
     * is published and returns a {@link ListenableFuture} that resolves to parameter data
     * when complete
     *
     * @param productIds must not be empty
     * @param parameterId must not be {@literal null}
     * @return a {@link ListenableFuture} that resolves with the data when complete
     */
    public ListenableFuture<ResponseEntity<List<Specs>>> findAllByProductIdsAndParameterIdWithPublishedStatus(
            final List<Integer> productIds, final Integer parameterId) {

        Assert.notNull(productIds);
        Assert.notEmpty(productIds);
        Assert.notNull(parameterId);

        final HttpHeaders headers = new HttpHeaders();
        final HttpEntity<?> entity = new HttpEntity<>(headers);
        final Map<String, Object> variables = new HashMap<>();
        variables.put("productIds", productIds);
        variables.put("parameterId", parameterId);

        try {
            final String url = UriTemplate.fromTemplate(baseUrl + SEARCH_URL).expand(
                    variables);
            logger.debug("Sending request to URI: {}", url);

            return restTemplate.exchange(url, HttpMethod.GET, entity,
                    new ParameterizedTypeReference<List<Specs>>() {
                    });
        } catch (VariableExpansionException | MalformedUriTemplateException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the {@link AsyncRestTemplate} used by this client
     *
     * @return the {@link AsyncRestTemplate} used by this client
     */
    public AsyncRestTemplate getRestTemplate() {
        return restTemplate;
    }

    /**
     * Set the {@link AsyncRestTemplate} that this client should use.
     *
     * @param restTemplate {@link AsyncRestTemplate} that this client should use.
     */
    public void setRestTemplate(final AsyncRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterPropertiesSet() {
        if (restTemplate == null) {
            restTemplate = new AsyncRestTemplate();
        }
    }

}
