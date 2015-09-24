/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springsource.restbucks.order.web;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springsource.restbucks.AbstractWebIntegrationTest;

/**
 * Integration test for REST resources exposed by Spring Data REST.
 * 
 * @author Oliver Gierke
 */
public class OrderResourceIntegrationTest extends AbstractWebIntegrationTest {

	@Test
	public void exposesOrdersResourceViaRootResource() throws Exception {

		MockHttpServletResponse response = mvc.perform(get("/")).//
				andDo(MockMvcResultHandlers.print()).//
				andExpect(status().isOk()). //
				andExpect(content().contentType(MediaTypes.HAL_JSON)). //
				andExpect(jsonPath("$._links.restbucks:orders.href", notNullValue())). //
				andReturn().getResponse();
		
		LinkDiscoverer discoverer = links.getLinkDiscovererFor(response.getContentType());
		Link link = discoverer.findLinkWithRel("profile", response.getContentAsString());

		response = mvc.perform(get(link.getHref())).//
				andDo(MockMvcResultHandlers.print()).//
				andExpect(status().isOk()). //
				andReturn().getResponse();

		discoverer = links.getLinkDiscovererFor(response.getContentType());
		link = discoverer.findLinkWithRel("restbucks:orders", response.getContentAsString());

		mvc.perform(get(link.getHref()).accept("application/schema+json")).//
				andDo(MockMvcResultHandlers.print()).//
				andExpect(status().isOk()). //
				andExpect(content().contentType("application/schema+json")). //
				andDo(document("order-schema"));

	}

	@Test
	public void exposesOrdersAsJsonSchema() throws Exception {


	}
}
