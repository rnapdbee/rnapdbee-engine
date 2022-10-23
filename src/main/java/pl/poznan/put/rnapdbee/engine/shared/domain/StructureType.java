package pl.poznan.put.rnapdbee.engine.shared.domain;

/**
 * Enum for Structure Type
 */
public enum StructureType {
    STRUCTURE_2D/*("2D")*/,
    STRUCTURE_3D/*("3D");*/

  /*private final String description;

  StructureType(final String description) {
    this.description = description;
  }

  public static StructureType fromDescription(final String description) {
    for (final StructureType type : StructureType.values()) {
      if (type.description.equals(description)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown StructureType: " + description);
  }

  @Override
  public String toString() {
    return description;
  }*/
}
