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

import java.time.Duration;
import java.util.function.Function;

import io.pivotal.literx.domain.User;
import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to deal with errors.
 *
 * @author Sebastien Deleuze
 * @see Exceptions#propagate(Throwable)
 * @see Hooks#onOperator(Function)
 */
public class Part07Errors {

//========================================================================================

	@Test
	public void monoWithValueInsteadOfError() {
		Mono<User> mono = betterCallSaulForBogusMono(Mono.error(new IllegalStateException()));
		StepVerifier.create(mono)
				.expectNext(User.SAUL)
				.expectComplete()
				.verify();

		mono = betterCallSaulForBogusMono(Mono.just(User.SKYLER));
		StepVerifier.create(mono)
				.expectNext(User.SKYLER)
				.expectComplete()
				.verify();
	}

	private Mono<User> betterCallSaulForBogusMono(Mono<User> mono) {
		return mono.otherwise(e -> Mono.just(User.SAUL));
	}

//========================================================================================

	@Test
	public void fluxWithValueInsteadOfError() {
		Flux<User> flux = betterCallSaulAndJesseForBogusFlux(Flux.error(new IllegalStateException()));
		StepVerifier.create(flux)
				.expectNext(User.SAUL, User.JESSE)
				.expectComplete()
				.verify();

		flux = betterCallSaulAndJesseForBogusFlux(Flux.just(User.SKYLER, User.WALTER));
		StepVerifier.create(flux)
				.expectNext(User.SKYLER, User.WALTER)
				.expectComplete()
				.verify();
	}

	private Flux<User> betterCallSaulAndJesseForBogusFlux(Flux<User> flux) {
		return flux.onErrorResumeWith(e -> Flux.just(User.SAUL, User.JESSE));
	}

//========================================================================================

	@Test
	public void handleCheckedExceptions() {
		Flux<User> flux = capitalizeMany(Flux.just(User.SAUL, User.JESSE));

		StepVerifier.create(flux)
				.expectError(GetOutOfHereException.class)
				.verify();
	}

	private Flux<User> capitalizeMany(Flux<User> flux) {
		return flux.flatMap(user -> {
			try {
				return Mono.just(capitalizeUser(user));
			} catch (GetOutOfHereException e) {
				return Mono.error(e);
			}
		});
	}

	private User capitalizeUser(User user) throws GetOutOfHereException {
		if (user.equals(User.SAUL)) {
			throw new GetOutOfHereException();
		}
		return new User(user.getUsername(), user.getFirstname(), user.getLastname());
	}

	private class GetOutOfHereException extends Exception {
	}

}
