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

package com.etilize.product.specs.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.etilize.commons.rest.RepositoryLinksResourceProcessor;
import com.etilize.commons.web.filter.SimpleCORSFilter;
import com.etilize.product.specs.SpecsController;

/**
 * Houses all the rest related configurations
 *
 * @author Faisal Feroz
 *
 */
@Configuration
public class RestConfig extends RepositoryRestMvcConfiguration {

    @Bean
    public Validator jsr303Validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public Filter simpleCORSFilter() {
        return new SimpleCORSFilter();
    }

    @Bean
    public RepositoryLinksResourceProcessor repositoryLinksResourceProcessor() {
        final List<Class<?>> rootControllers = new ArrayList<>();
        rootControllers.add(SpecsController.class);
        return new RepositoryLinksResourceProcessor(rootControllers);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration#
     * configureRepositoryRestConfiguration
     * (org.springframework.data.rest.core.config.RepositoryRestConfiguration)
     */
    @Override
    protected void configureRepositoryRestConfiguration(
            final RepositoryRestConfiguration config) {
        config.setReturnBodyOnCreate(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration#
     * configureValidatingRepositoryEventListener
     * (org.springframework.data.rest.core.event.ValidatingRepositoryEventListener)
     */
    @Override
    protected void configureValidatingRepositoryEventListener(
            ValidatingRepositoryEventListener validatingListener) {
        validatingListener.addValidator("beforeCreate", jsr303Validator());
    }
}
