package dk.nykredit.api.capabilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Parser to be used to parse capabilities.
 * @param <C> The capability handled by parser instance
 */
public class CapabilityParser<C> {
    private final Pattern pattern;
    private final Function<String, Optional<C>> factory;
    private final BiPredicate<C, C> duplicate;
    
    public CapabilityParser(String regex, Function<String, Optional<C>> factory) {
        this(regex, factory, (c1, c2) -> false);
    }
    
    public CapabilityParser(String regex, Function<String, Optional<C>> factory, BiPredicate<C, C> duplicate) {
        this.pattern = Pattern.compile(regex);
        this.factory = factory;
        this.duplicate = duplicate;
    }

    public List<C> parse(String capability) {
        if (capability == null || capability.isEmpty()) {
            return Collections.emptyList();
        }
        
        if (!pattern.matcher(capability).matches()) {
            return Collections.emptyList();
        }
        
        String sanitized = Sanitizer.sanitize(capability, true, true);
        List<String> tokens = Arrays.asList(sanitized.split("\\|"));
        return tokens.stream()
                .map(factory::apply)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(ArrayList::new, this::accumulate, ArrayList::addAll);
    }
    
    private void accumulate(List<C> list, C capability) {
        if (!list.stream().anyMatch((c) -> duplicate.test(c, capability))) {
            list.add(capability);
        }
    }
    
}
