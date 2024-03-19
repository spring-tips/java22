package com.example.demo;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

@Component
class Gatherers implements LanguageDemonstrationRunner {

    // viktor klang! the legend of klang continues!
    //https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/util/stream/Gatherer.html
    private static <T, R> Gatherer<T, ?, R> scan(
            Supplier<R> initial,
            BiFunction<? super R, ? super T, ? extends R> scanner) {

        class State {
            R current = initial.get();
        }

        return Gatherer.<T, State, R>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.current = scanner.apply(state.current, element);
                    return downstream.push(state.current);
                })
        );
    }

    @Override
    public void run() {

        var listOfNumberStrings = Stream
                .of(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .gather(scan(() -> "", (string, number) -> string + number)
                        .andThen(java.util.stream.Gatherers.mapConcurrent(10,
                                s -> s.toUpperCase(Locale.ROOT)))
                )
                .toList();

        System.out.println(listOfNumberStrings);

    }
}
