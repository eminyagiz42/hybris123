/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.concerttours.service.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.lang.InterruptedException;
import java.util.concurrent.TimeUnit;
import de.hybris.platform.core.Registry;
import org.springframework.jdbc.core.JdbcTemplate;

import static de.hybris.platform.concerttours.constants.ConcerttoursConstants.PLATFORM_LOGO_CODE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import de.hybris.platform.concerttours.service.ConcerttoursService;
import de.hybris.platform.concerttours.service.impl.DefaultConcerttoursService;


/**
 * This is an example of how the integration test should look like. {@link ServicelayerBaseTest} bootstraps platform so
 * you have an access to all Spring beans as well as database connection. It also ensures proper cleaning out of items
 * created during the test after it finishes. You can inject any Spring service using {@link Resource} annotation. Keep
 * in mind that by default it assumes that annotated field name matches the Spring Bean ID.
 */
@IntegrationTest
public class DefaultConcerttoursServiceIntegrationTest extends ServicelayerBaseTest
{
	@Resource
	private ConcerttoursService concerttoursService;
	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Before
	public void setUp() throws Exception
	{
		try {
	    	Thread.sleep(TimeUnit.SECONDS.toMillis(1));
	        new JdbcTemplate(Registry.getCurrentTenant().getDataSource()).execute("CHECKPOINT");
	        Thread.sleep(TimeUnit.SECONDS.toMillis(1));
    	}
    	catch (InterruptedException exc) {}
		concerttoursService.createLogo(PLATFORM_LOGO_CODE);
	}

	@Test
	public void shouldReturnProperUrlForLogo() throws Exception
	{
		// given
		final String logoCode = "concerttoursPlatformLogo";

		// when
		final String logoUrl = concerttoursService.getHybrisLogoUrl(logoCode);

		// then
		assertThat(logoUrl).isNotNull();
		assertThat(logoUrl).isEqualTo(findLogoMedia(logoCode).getURL());
	}

	private MediaModel findLogoMedia(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery("SELECT {PK} FROM {Media} WHERE {code}=?code");
		fQuery.addQueryParameter("code", logoCode);

		return flexibleSearchService.searchUnique(fQuery);
	}

}
