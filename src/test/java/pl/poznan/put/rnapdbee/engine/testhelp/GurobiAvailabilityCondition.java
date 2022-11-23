package pl.poznan.put.rnapdbee.engine.testhelp;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.poznan.put.rnapdbee.engine.testhelp.shared.configuration.TestConverterConfiguration;

import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

public class GurobiAvailabilityCondition implements ExecutionCondition {

    private static final Logger logger = LoggerFactory.getLogger(GurobiAvailabilityCondition.class);

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        final var optional = findAnnotation(context.getElement(), SkipIfGurobiIsNotAvailable.class);
        if (optional.isEmpty()) {
            return ConditionEvaluationResult.enabled("Annotation not used, continuing test execution");
        }

        boolean isGurobiAvailable = TestConverterConfiguration.checkIfGurobiIsSetUpCorrectly();
        if (isGurobiAvailable) {
            logger.info("Gurobi is installed and license is set on this machine");
            return ConditionEvaluationResult.enabled("Gurobi is available on this machine, continuing test execution");
        } else {
            logger.warn("Gurobi is not installed or license is not set on this machine");
            return ConditionEvaluationResult.disabled("Gurobi is not available on this machine, skipping test execution");
        }
    }
}
