package pl.poznan.put.rnapdbee.engine.shared.converter.boundary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.poznan.put.rnapdbee.engine.shared.basepair.exception.AdaptersErrorException;
import pl.poznan.put.rnapdbee.engine.shared.exception.ConverterException;
import pl.poznan.put.rnapdbee.engine.shared.integration.adapters.boundary.RnaPDBeeAdaptersCaller;
import pl.poznan.put.structure.formats.BpSeq;
import pl.poznan.put.structure.formats.Converter;
import pl.poznan.put.structure.formats.DotBracket;

@Service
public class ExternalConverter implements Converter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalConverter.class);
    private final RnaPDBeeAdaptersCaller adaptersCaller;

    @Autowired
    public ExternalConverter(RnaPDBeeAdaptersCaller rnaPDBeeAdaptersCaller) {
        this.adaptersCaller = rnaPDBeeAdaptersCaller;
    }

    @Override
    public DotBracket convert(BpSeq bpSeq) {
        try {
            return adaptersCaller.performBpSeqConversion(bpSeq);
        } catch (AdaptersErrorException e) {
            LOGGER.error("Error during conversion via adapters", e);
            throw new ConverterException();
        }
    }
}
