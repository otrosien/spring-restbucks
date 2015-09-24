/*
 * Copyright 2013 the original author or authors.
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
package org.springsource.restbucks.engine.web;

import static org.springframework.restdocs.RestDocumentation.document;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Properties;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkDiscoverer;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.hypermedia.HypermediaDocumentation;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springsource.restbucks.AbstractWebIntegrationTest;
import org.springsource.restbucks.Restbucks;

/**
 * Integration tests for {@link EngineController}.
 * 
 * @author Oliver Gierke
 */
public class EngineControllerIntegrationTests extends AbstractWebIntegrationTest {

	private final String ENGINE_REL = Restbucks.CURIE_NAMESPACE + ":" + EngineController.ENGINE_REL;

	@Test
	public void customControllerReturnsDefaultMediaType() throws Exception {

		Properties props = PropertiesLoaderUtils.loadProperties(new ClassPathResource("/rest-messages.properties"));

		MockHttpServletResponse response = mvc.perform(get("/")).//
				andDo(MockMvcResultHandlers.print()).//
				andExpect(linkWithRelIsPresent(ENGINE_REL)). //
				andDo(document("index",
						HypermediaDocumentation.links(
								linkWithRel("restbucks:orders").description(props.getProperty("_links.orders.title")),
								linkWithRel(ENGINE_REL).description(props.getProperty("_links.engine.title")),
								linkWithRel("restbucks:pages").description(props.getProperty("_links.pages.title")),
								linkWithRel("profile").description(props.getProperty("_links.profile.title")),
								linkWithRel("curies").description("{some.curies}")
				))).
				andReturn().getResponse();

		LinkDiscoverer discoverer = links.getLinkDiscovererFor(response.getContentType());
		Link link = discoverer.findLinkWithRel(ENGINE_REL, response.getContentAsString());

		// the list of orders in progress is empty.
		mvc.perform(get(link.getHref())). //
				andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON)).
				andDo(document("engine"));
	}
}
