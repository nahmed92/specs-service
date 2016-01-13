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

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SpecsLinks {

    public static final String REL_SEARCH = "search";

    public static final String REL_SPECS = "specs";

    public static final String BASE_MAPPING_SPECS = "/" + REL_SPECS;

    public static final String BASE_MAPPING_SEARCH = SpecsLinks.BASE_MAPPING_SPECS + "/"
            + REL_SEARCH;

    private static final String PARAMETER_NAME_TEMPLATE_PATTERN = "{?%s}";

    static final String REL_FIND_ALL_BY_PRODUCT_IDS_AND_PARAMETER_ID_WITH_PUBLISHED_STATUS = "findAllByProductIdsAndParameterIdWithPublishedStatus";

    static final String REL_FIND_PRODUCTITEM_BY_DATE_RANGE_CATEGORYID_AND_TEMPLATEID = "findProductItemByDateRangeCategoryIdAndTemplateId";

    /**
     * Returns the search link
     *
     * @return search {@link Link}
     */
    public Link getSearchLink() {
        return linkTo(SpecsSearchController.class).withRel(REL_SEARCH);
    }

    /**
     * Generates link for the passed in controller with rel and passed in query parameters
     *
     * @param controller
     * @param rel
     * @param parameters
     * @return link for the passed in controller with rel and passed in query parameters
     */
    public <T> Link linkFor(final Class<T> controller, final String rel,
            final String... parameters) {
        final String parameterTemplateVariable = getParameterTemplateVariable(Arrays.asList(parameters));
        final String href = ControllerLinkBuilder.linkTo(controller).slash(rel).toString().concat(
                parameterTemplateVariable);

        return new Link(href, rel);
    }

    private String getParameterTemplateVariable(final Collection<String> parameters) {
        final String parameterString = StringUtils.collectionToCommaDelimitedString(parameters);
        return parameters.isEmpty() ? "" : String.format(PARAMETER_NAME_TEMPLATE_PATTERN,
                parameterString);
    }
}
