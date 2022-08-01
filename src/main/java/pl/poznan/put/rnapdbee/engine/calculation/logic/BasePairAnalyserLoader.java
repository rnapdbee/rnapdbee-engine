package pl.poznan.put.rnapdbee.engine.calculation.logic;

import edu.put.rnapdbee.analysis.BasePairAnalyzer;
import edu.put.rnapdbee.analysis.BasePairAnalyzerBuilder;
import edu.put.rnapdbee.enums.BasePairAnalyzerEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class BasePairAnalyserLoader {

    @Value("${tools.dssr}")
    private Resource dssr;

    @Value("${tools.fr3d}")
    private File fr3d;

    @Value("${tools.fr3d-python}")
    private File fr3dPython;

    @Value("${tools.mcannotate}")
    private Resource mcAnnotate;

    @Value("${tools.rnaview}")
    private Resource rnaView;

    @Value("${tools.rnaview.basepars}")
    private Resource rnaViewBaseParameters;

    @Value("${system.octave}")
    private File octave;

    @Value("${system.python2}")
    private File python2;

    @Value("${tools.pdbx}")
    private Resource pdbx;

    public BasePairAnalyzer loadBasePairAnalyzer(final BasePairAnalyzerEnum analyzerEnum, final boolean analyzeHelices)
            throws IOException {
        final BasePairAnalyzerBuilder builder = new BasePairAnalyzerBuilder(analyzerEnum);
        return builder
                .rnaviewExecutable(rnaView.getFile())
                .rnaviewBasepars(rnaViewBaseParameters.getFile())
                .mcannotateExecutable(mcAnnotate.getFile())
                .dssrExecutable(dssr.getFile())
                .dssrAnalyzeHelices(analyzeHelices)
                .fr3dOctave(octave)
                .fr3dPython2(python2)
                .fr3dDirectory(fr3d)
                .fr3dPythonDirectory(fr3dPython)
                .fr3dPdbxDirectory(pdbx.getFile())
                .build();
    }
}
