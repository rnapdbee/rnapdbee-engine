{
  pkgs,
  lib,
  config,
  ...
}:
{
  languages.java = {
    enable = true;
    jdk.package = pkgs.jdk17;
    maven.enable = true;
  };
}
