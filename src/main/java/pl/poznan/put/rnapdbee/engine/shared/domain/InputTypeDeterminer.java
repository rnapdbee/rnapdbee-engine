package pl.poznan.put.rnapdbee.engine.shared.domain;

import org.springframework.stereotype.Component;

@Component
public class InputTypeDeterminer {

    public InputType detectTertiaryInputTypeFromFileName(String filename) {
        for (InputType inputType : InputType.TERTIARY_INPUT_TYPES) {
            if (filename.toLowerCase().contains(inputType.getFileExtension())) {
                return inputType;
            }
        }
        throw new IllegalArgumentException("unknown file extension provided");
    }
}
