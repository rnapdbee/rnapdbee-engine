package pl.poznan.put.rnapdbee.engine.shared.domain;

import org.springframework.stereotype.Component;
import pl.poznan.put.rnapdbee.engine.shared.exception.UnknownFileExtensionException;

@Component
public class InputTypeDeterminer {

    public InputType detectTertiaryInputTypeFromFileName(String filename) {
        for (InputType inputType : InputType.TERTIARY_INPUT_TYPES) {
            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new UnknownFileExtensionException("Unknown tertiary file extension provided");
    }

    public InputType detectSecondaryInputTypeFromFileName(String filename) {
        for (InputType inputType : InputType.SECONDARY_INPUT_TYPES) {
            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new UnknownFileExtensionException("Unknown secondary file extension provided");
    }
}
