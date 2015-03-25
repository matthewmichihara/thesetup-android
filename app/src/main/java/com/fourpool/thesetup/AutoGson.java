package com.fourpool.thesetup;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks an {@link auto.parcel.AutoParcel @AutoParcel}-annotated type for proper Gson
 * serialization.
 * <p>
 * This annotation is needed because the {@linkplain Retention retention} of {@code @AutoValue}
 * does not allow reflection at runtime.
 */
@Target(TYPE) @Retention(RUNTIME)
public @interface AutoGson {
}