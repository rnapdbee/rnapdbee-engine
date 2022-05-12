package pl.poznan.put.rnapdbee.engine.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO class for Atom - taken from rnapdbee-common project
 */
public class Atom {
    @JsonProperty("x")
    private double x;

    @JsonProperty("y")
    private double y;

    @JsonProperty("z")
    private double z;

    @JsonProperty("atomName")
    private String atomName;

    @JsonProperty("other")
    private String other;

    @JsonProperty("originalTemplateAtomName")
    private String originalTemplateAtomName;

        public double getX() {
                return x;
        }

        public double getY() {
                return y;
        }

        public double getZ() {
                return z;
        }

        public String getAtomName() {
                return atomName;
        }

        public String getOther() {
                return other;
        }

        public String getOriginalTemplateAtomName() {
                return originalTemplateAtomName;
        }
}

