package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Learn how to merge flux.
 *
 * @author Sebastien Deleuze
 */
public class Part05Merge {

	private final static User MARIE = new User("mschrader", "Marie", "Schrader");
	private final static User MIKE = new User("mehrmantraut", "Mike", "Ehrmantraut");

	private ReactiveRepository<User> repository1 = new ReactiveUserRepository(500);
	private ReactiveRepository<User> repository2 = new ReactiveUserRepository(MARIE, MIKE);

//========================================================================================

	@Test
	public void mergeWithInterleave() {
		Flux<User> flux = mergeFluxWithInterleave(repository1.findAll(), repository2.findAll());
		StepVerifier.create(flux)
				.expectNext(MARIE, MIKE, User.SKYLER, User.JESSE, User.WALTER, User.SAUL)
				.expectComplete()
				.verify();
	}

	private Flux<User> mergeFluxWithInterleave(Flux<User> flux1, Flux<User> flux2) {
		return flux1.mergeWith(flux2);
	}

//========================================================================================

	@Test
	public void mergeWithNoInterleave() {
		Flux<User> flux = mergeFluxWithNoInterleave(repository1.findAll(), repository2.findAll());
		StepVerifier.create(flux)
				.expectNext(User.SKYLER, User.JESSE, User.WALTER, User.SAUL, MARIE, MIKE)
				.expectComplete()
				.verify();
	}

	private Flux<User> mergeFluxWithNoInterleave(Flux<User> flux1, Flux<User> flux2) {
		return flux1.concatWith(flux2);
	}

//========================================================================================

	@Test
	public void multipleMonoToFlux() {
		Mono<User> skylerMono = repository1.findFirst();
		Mono<User> marieMono = repository2.findFirst();
		Flux<User> flux = createFluxFromMultipleMono(skylerMono, marieMono);
		StepVerifier.create(flux)
				.expectNext(User.SKYLER, MARIE)
				.expectComplete()
				.verify();
	}

	private Flux<User> createFluxFromMultipleMono(Mono<User> mono1, Mono<User> mono2) {
		return Flux.concat(mono1, mono2);
	}

}
