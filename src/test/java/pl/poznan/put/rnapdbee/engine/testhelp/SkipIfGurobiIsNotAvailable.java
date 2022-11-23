package pl.poznan.put.rnapdbee.engine.testhelp;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(GurobiAvailabilityCondition.class)
public @interface SkipIfGurobiIsNotAvailable {
}
