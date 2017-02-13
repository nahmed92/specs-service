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

import static com.etilize.product.specs.SpecsLinks.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.etilize.commons.controller.AbstractRepositoryRestController;

/**
 * Controller which houses methods related to search of {@link Specs}
 *
 * @author Faisal Feroz
 *
 */
@RequestMapping(value = BASE_MAPPING_SEARCH)
@RestResource(rel = REL_SEARCH, path = BASE_MAPPING_SEARCH)
@RestController
@ExposesResourceFor(Specs.class)
@RepositoryRestController
public class SpecsSearchController extends AbstractRepositoryRestController {

    /**
     * request argument for product ids
     */
    static final String PARAM_PRODUCT_IDS = "productIds";

    /**
     * rest request argument for parameter id
     */
    static final String PARAM_PARAMETER_ID = "parameterId";

    static final String PARAM_TEMPLATE_ID = "templateId";

    static final String PARAM_FROM_DATE = "startDate";

    static final String PARAM_TO_DATE = "endDate";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SpecsRepository repo;

    private final SpecsLinks specsLinks;

    /**
     * Constructor
     *
     * @param repo - {@link SpecsRepository} instance
     * @param specsLinks - {@link SpecsLinks} instance
     * @param pagedResourcesAssembler - {@link PagedResourcesAssembler} for returning
     *        paged resources
     */
    @Autowired
    public SpecsSearchController(final SpecsRepository repo, final SpecsLinks specsLinks,
            final PagedResourcesAssembler<Object> pagedResourcesAssembler) {
        super(pagedResourcesAssembler);
        this.specsLinks = specsLinks;
        this.repo = repo;
    }

    /**
     * <code>GET /specs/search</code> - Exposes links to the individual search resources
     * exposed.
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResourceSupport listSearches() {
        final Link findProductByParameterIdswithPublishStatusLink = specsLinks.linkFor(
                this.getClass(),
                REL_FIND_ALL_BY_PRODUCT_IDS_AND_PARAMETER_ID_WITH_PUBLISHED_STATUS,
                PARAM_PRODUCT_IDS, PARAM_PARAMETER_ID);
        final Link findProductItemByDateRangeAndTemplateId = specsLinks.linkFor(
                this.getClass(), REL_FIND_PRODUCT_ITEM_BY_DATE_RANGE_AND_TEMPLATE_ID,
                PARAM_FROM_DATE, PARAM_TO_DATE, PARAM_TEMPLATE_ID);
        final Links links = new Links(findProductByParameterIdswithPublishStatusLink,
                findProductItemByDateRangeAndTemplateId);

        final ResourceSupport result = new ResourceSupport();
        result.add(links);

        return result;
    }

    /**
     * <code>GET /specs/search/findAllByProductIdsAndParameterIdWithPublishedStatus</code>
     * - returns the specs for passed in productIds and parameterId
     *
     * @param productIds must not be {@literal null} or empty
     * @param parameterId must not be {@literal null}
     * @return the {@link List} of {@link Specs} which matches the given productIds and
     *         parameterId
     */
    @RequestMapping(value = "/"
            + REL_FIND_ALL_BY_PRODUCT_IDS_AND_PARAMETER_ID_WITH_PUBLISHED_STATUS, method = RequestMethod.GET)
    public ResponseEntity<List<Specs>> findAllByProductIdsAndParameterIdWithPublishedStatus(
            @RequestParam(PARAM_PRODUCT_IDS) final List<Integer> productIds,
            @RequestParam(PARAM_PARAMETER_ID) final Integer parameterId) {
        Assert.notEmpty(productIds);
        Assert.notNull(parameterId);

        final long time = System.nanoTime();
        final List<Specs> specs = repo.findAllByProductIdAndParameterIdWithPublishedStatus(
                productIds, parameterId);
        logger.debug("Query Time {} ms",
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - time));

        return new ResponseEntity<>(specs, HttpStatus.OK);
    }

    /**
     * <code>GET /specs/search/findProductItemByDateRangeAndTemplateId</code> - returns
     * the specs for passed in productIds and parameterId
     *
     * @param startDate must not be {@literal null} or empty
     * @param endDate must not be {@literal null}
     * @param templateId must not be {@literal null}
     * @return the {@link List} of {@link ProductItem} which matches the given productIds
     *         and parameterId
     */
    @RequestMapping(value = "/" + REL_FIND_PRODUCT_ITEM_BY_DATE_RANGE_AND_TEMPLATE_ID, method = RequestMethod.GET)
    public ResponseEntity<List<ProductItem>> findProductItemByDateRangeAndTemplateId(
            @RequestParam(PARAM_FROM_DATE) final String startDate,
            @RequestParam(PARAM_TO_DATE) final String endDate,
            @RequestParam(PARAM_TEMPLATE_ID) final Integer templateId) {
        Assert.hasText(startDate);
        Assert.hasText(endDate);
        Assert.notNull(templateId);

        final List<ProductItem> productItems = repo.findProductItemByDateRangeAndTemplateId(
                startDate, endDate, templateId);

        return new ResponseEntity<List<ProductItem>>(productItems, HttpStatus.OK);
    }

}
