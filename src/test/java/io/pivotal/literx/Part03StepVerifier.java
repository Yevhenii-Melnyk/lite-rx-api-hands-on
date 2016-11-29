/*
 * Copyright 2002-2016 the original author or authors.
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

package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Supplier;

import static org.junit.Assert.fail;

/**
 * Learn how to use StepVerifier to test Mono, Flux or any other kind of Reactive Streams Publisher.
 *
 * @author Sebastien Deleuze
 * @see <a href="http://next.projectreactor.io/ext/docs/api/reactor/test/StepVerifier.html">StepVerifier Javadoc</a>
 */
public class Part03StepVerifier {

//========================================================================================

	@Test
	public void expectElementsThenComplete() {
		expectFooBarComplete(Flux.just("foo", "bar"));
	}

	private void expectFooBarComplete(Flux<String> flux) {
		StepVerifier.create(flux)
				.expectNext("foo", "bar")
				.expectComplete();
	}

//========================================================================================

	@Test
	public void expect2ElementsThenError() {
		expectFooBarError(Flux.just("foo", "bar").concatWith(Mono.error(new RuntimeException())));
	}

	private void expectFooBarError(Flux<String> flux) {
		StepVerifier.create(flux)
				.expectNext("foo", "bar")
				.expectError(RuntimeException.class);
	}

//========================================================================================

	@Test
	public void expectElementsWithThenComplete() {
		expectSkylerJesseComplete(Flux.just(new User("swhite", null, null), new User("jpinkman", null, null)));
	}

	private void expectSkylerJesseComplete(Flux<User> flux) {
		StepVerifier.create(flux)
				.expectNextMatches(user -> "swhite".equals(user.getUsername()))
				.expectNextMatches(user -> "jpinkman".equals(user.getUsername()))
				.expectComplete();
	}

//========================================================================================

	@Test
	public void count() {
		expect10Elements(Flux.interval(Duration.ofSeconds(1)).take(10));
	}

	private void expect10Elements(Flux<Long> flux) {
		StepVerifier.create(flux)
				.expectNextCount(10)
				.expectComplete()
				.verify(Duration.ofSeconds(11));

	}

//========================================================================================

	@Test
	public void countWithVirtualTime() {
		expect3600Elements(() -> Flux.interval(Duration.ofSeconds(1)).take(3600));
	}

	private void expect3600Elements(Supplier<Flux<Long>> supplier) {
		StepVerifier.withVirtualTime(3600, supplier)
				.expectNext(1L)
				.expectComplete();
	}

}
