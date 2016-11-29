package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Iterator;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Learn how to turn Reactive API to blocking one.
 *
 * @author Sebastien Deleuze
 */
public class Part10ReactiveToBlocking {

	private ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void mono() {
		Mono<User> mono = repository.findFirst();
		User user = monoToValue(mono);
		assertEquals(User.SKYLER, user);
	}

	private User monoToValue(Mono<User> mono) {
		return mono.block();
	}

//========================================================================================

	@Test
	public void flux() {
		Flux<User> flux = repository.findAll();
		Iterable<User> users = fluxToValues(flux);
		Iterator<User> it = users.iterator();
		assertEquals(User.SKYLER, it.next());
		assertEquals(User.JESSE, it.next());
		assertEquals(User.WALTER, it.next());
		assertEquals(User.SAUL, it.next());
		assertFalse(it.hasNext());
	}

	private Iterable<User> fluxToValues(Flux<User> flux) {
		return flux.toIterable();
	}

}
