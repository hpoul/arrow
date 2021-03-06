package arrow.free.instances

import arrow.Kind
import arrow.core.FunctionK
import arrow.free.FreeApplicative
import arrow.free.FreeApplicativePartialOf
import arrow.free.fix
import arrow.instance
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad

@instance(FreeApplicative::class)
interface FreeApplicativeFunctorInstance<S> : Functor<FreeApplicativePartialOf<S>> {
  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.map(f: (A) -> B): FreeApplicative<S, B> = fix().map(f)
}

@instance(FreeApplicative::class)
interface FreeApplicativeApplicativeInstance<S> : FreeApplicativeFunctorInstance<S>, Applicative<FreeApplicativePartialOf<S>> {
  override fun <A> just(a: A): FreeApplicative<S, A> = FreeApplicative.just(a)

  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.ap(ff: Kind<FreeApplicativePartialOf<S>, (A) -> B>): FreeApplicative<S, B> =
    fix().ap(ff.fix())

  override fun <A, B> Kind<FreeApplicativePartialOf<S>, A>.map(f: (A) -> B): FreeApplicative<S, B> = fix().map(f)
}

interface FreeApplicativeEq<F, G, A> : Eq<Kind<FreeApplicativePartialOf<F>, A>> {
  fun MG(): Monad<G>

  fun FK(): FunctionK<F, G>

  override fun Kind<FreeApplicativePartialOf<F>, A>.eqv(b: Kind<FreeApplicativePartialOf<F>, A>): Boolean =
    fix().foldMap(FK(), MG()) == b.fix().foldMap(FK(), MG())
}

@Suppress("UNUSED_PARAMETER")
fun <F, G, A> FreeApplicative.Companion.eq(FK: FunctionK<F, G>, MG: Monad<G>, dummy: Unit = Unit): FreeApplicativeEq<F, G, A> =
  object : FreeApplicativeEq<F, G, A> {
    override fun FK(): FunctionK<F, G> = FK

    override fun MG(): arrow.typeclasses.Monad<G> = MG
  }
