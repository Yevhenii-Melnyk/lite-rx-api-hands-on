package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to transform values.
 *
 * @author Sebastien Deleuze
 */
public class Part04Transform {

	private ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void transformMono() {
		Mono<User> mono = repository.findFirst();
		StepVerifier.create(capitalizeOne(mono))
				.expectNext(new User("SWHITE", "SKYLER", "WHITE"))
				.expectComplete()
				.verify();
	}

	private Mono<User> capitalizeOne(Mono<User> mono) {
		return mono.map(user -> new User(
				user.getUsername().toUpperCase(),
				user.getFirstname().toUpperCase(),
				user.getLastname().toUpperCase()
		));
	}

//========================================================================================

	@Test
	public void transformFlux() {
		Flux<User> flux = repository.findAll();
		StepVerifier.create(capitalizeMany(flux))
				.expectNext(
						new User("SWHITE", "SKYLER", "WHITE"),
						new User("JPINKMAN", "JESSE", "PINKMAN"),
						new User("WWHITE", "WALTER", "WHITE"),
						new User("SGOODMAN", "SAUL", "GOODMAN"))
				.expectComplete()
				.verify();
	}

	private Flux<User> capitalizeMany(Flux<User> flux) {
		return flux.map(user -> new User(
				user.getUsername().toUpperCase(),
				user.getFirstname().toUpperCase(),
				user.getLastname().toUpperCase()
		));
	}

//========================================================================================

	@Test
	public void asyncTransformFlux() {
		Flux<User> flux = repository.findAll();
		StepVerifier.create(asyncCapitalizeMany(flux))
				.expectNext(
						new User("SWHITE", "SKYLER", "WHITE"),
						new User("JPINKMAN", "JESSE", "PINKMAN"),
						new User("WWHITE", "WALTER", "WHITE"),
						new User("SGOODMAN", "SAUL", "GOODMAN"))
				.expectComplete()
				.verify();
	}

	private Flux<User> asyncCapitalizeMany(Flux<User> flux) {
		return flux.flatMap(this::asyncCapitalizeUser);
	}

	private Mono<User> asyncCapitalizeUser(User u) {
		return Mono.just(new User(u.getUsername().toUpperCase(), u.getFirstname().toUpperCase(), u.getLastname().toUpperCase()));
	}

}
